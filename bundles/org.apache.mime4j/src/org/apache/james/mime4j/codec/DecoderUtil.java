/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mime4j.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.util.CharsetUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static methods for decoding strings, byte arrays and encoded words.
 */
public class DecoderUtil {
    private static Log log = LogFactory.getLog(DecoderUtil.class);

    /**
     * Decodes a string containing quoted-printable encoded data.
     *
     * @param s the string to decode.
     * @return the decoded bytes.
     */
    public static byte[] decodeQuotedPrintable(String s) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            byte[] bytes = s.getBytes("US-ASCII");

            QuotedPrintableInputStream is = new QuotedPrintableInputStream(
                    new ByteArrayInputStream(bytes));

            int b = 0;
            while ((b = is.read()) != -1) {
                baos.write(b);
            }
        } catch (IOException e) {
            // This should never happen!
            log.error(e);
            throw new IllegalStateException(e);
        }

        return baos.toByteArray();
    }

    /**
     * Decodes a string containing base64 encoded data.
     *
     * @param s the string to decode.
     * @return the decoded bytes.
     */
    public static byte[] decodeBase64(String s) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            byte[] bytes = s.getBytes("US-ASCII");

            Base64InputStream is = new Base64InputStream(
                    new ByteArrayInputStream(bytes));

            int b = 0;
            while ((b = is.read()) != -1) {
                baos.write(b);
            }
        } catch (IOException e) {
            // This should never happen!
            log.error(e);
            throw new IllegalStateException(e);
        }

        return baos.toByteArray();
    }

    /**
     * Decodes an encoded text encoded with the 'B' encoding (described in
     * RFC 2047) found in a header field body.
     *
     * @param encodedText the encoded text to decode.
     * @param charset     the Java charset to use.
     * @return the decoded string.
     * @throws UnsupportedEncodingException if the given Java charset isn't
     *                                      supported.
     */
    public static String decodeB(String encodedText, String charset)
            throws UnsupportedEncodingException {
        byte[] decodedBytes = decodeBase64(encodedText);
        return new String(decodedBytes, charset);
    }

    /**
     * Decodes an encoded text encoded with the 'Q' encoding (described in
     * RFC 2047) found in a header field body.
     *
     * @param encodedText the encoded text to decode.
     * @param charset     the Java charset to use.
     * @return the decoded string.
     * @throws UnsupportedEncodingException if the given Java charset isn't
     *                                      supported.
     */
    public static String decodeQ(String encodedText, String charset)
            throws UnsupportedEncodingException {
        encodedText = replaceUnderscores(encodedText);

        byte[] decodedBytes = decodeQuotedPrintable(encodedText);
        return new String(decodedBytes, charset);
    }


    final static Pattern parseEncodedWords = Pattern.compile("(.*?)=\\?([^\\?]*?)\\?([^\\?]*?)\\?([^\\?]*?)\\?=", Pattern.DOTALL);

    /**
     * Decodes a string containing encoded words as defined by RFC 2047.
     * Encoded words in have the form
     * =?charset?enc?Encoded word?= where enc is either 'Q' or 'q' for
     * quoted-printable and 'B' or 'b' for Base64.
     *
     * @param body the string to decode.
     * @return the decoded string.
     */
    public static String decodeEncodedWords(String body) {

        StringBuffer sb = new StringBuffer();
        boolean previousWasEncoded = false;

        final Matcher matcher = parseEncodedWords.matcher(body);
        while (matcher.find()) {
            String separator = matcher.group(1);
            String mimeCharset = matcher.group(2);
            String encoding = matcher.group(3);
            String encodedText = matcher.group(4);

            final String decoded = decodeEncodedWord(mimeCharset, encoding, encodedText);
            if (decoded == null) {
                matcher.appendReplacement(sb, matcher.group(0));
                previousWasEncoded = false;
            } else {
                if ((!previousWasEncoded) || (!CharsetUtil.isWhitespace(separator)))
                    sb.append(separator);
                matcher.appendReplacement(sb, decoded);
                previousWasEncoded = true;
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * Decodes one encoded word in a string as defined by RFC 2047.
     * Encoded words in have the form
     * =?charset?enc?Encoded word?= where enc is either 'Q' or 'q' for
     * quoted-printable and 'B' or 'b' for Base64.
     *
     * @param body  the string to decode.
     * @param begin the index of the first char of the encoded word to decode
     * @param end   the index of the last char of the encoded word to decode
     * @return the decoded string or null on error.
     */
    public static String decodeEncodedWord(String body, int begin, int end) {
        int qm1 = body.indexOf('?', begin + 2);
        if (qm1 == end - 2)
            return null;

        int qm2 = body.indexOf('?', qm1 + 1);
        if (qm2 == end - 2)
            return null;

        String mimeCharset = body.substring(begin + 2, qm1);
        String encoding = body.substring(qm1 + 1, qm2);
        String encodedText = body.substring(qm2 + 1, end - 2);

        return decodeEncodedWord(mimeCharset, encoding, encodedText);
    }

    /**
     * Decodes an encoded string with a given charset and encoding
     * enc is either 'Q' or 'q' for
     * quoted-printable and 'B' or 'b' for Base64.
     *
     * @param mimeCharset the charset, e.g. "UTF-8" or "ISO-8859-1"
     * @param encoding    'Q','q' for quoted-printable or 'B','b' for Base64
     * @param encodedText the encoded text
     * @return the decoded string or null on error.
     */
    private static String decodeEncodedWord(String mimeCharset, String encoding, String encodedText) {
        String charset = CharsetUtil.toJavaCharset(mimeCharset);
        if (charset == null) {
            if (log.isWarnEnabled()) {
                log.warn("MIME charset '" + mimeCharset + "' in encoded word '" +
                        recombineEncodedWord(mimeCharset, encoding, encodedText) + "' doesn't have a "
                        + "corresponding Java charset");
            }
            return null;
        } else if (!CharsetUtil.isDecodingSupported(charset)) {
            if (log.isWarnEnabled()) {
                log.warn("Current JDK doesn't support decoding of charset '"
                        + charset + "' (MIME charset '" + mimeCharset
                        + "' in encoded word '" + recombineEncodedWord(mimeCharset, encoding, encodedText)
                        + "')");
            }
            return null;
        }

        if (encodedText.length() == 0) {
            if (log.isWarnEnabled()) {
                log.warn("Missing encoded text in encoded word: '" + recombineEncodedWord(mimeCharset, encoding, encodedText) + "'");
            }
            return null;
        }

        try {
            if (encoding.equalsIgnoreCase("Q")) {
                return DecoderUtil.decodeQ(encodedText, charset);
            } else if (encoding.equalsIgnoreCase("B")) {
                return DecoderUtil.decodeB(encodedText, charset);
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("Warning: Unknown encoding in encoded word '" + recombineEncodedWord(mimeCharset, encoding, encodedText) + "'");
                }
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            // should not happen because of isDecodingSupported check above
            if (log.isWarnEnabled()) {
                log.warn("Unsupported encoding in encoded word '" + recombineEncodedWord(mimeCharset, encoding, encodedText) + "'", e);
            }
            return null;
        } catch (RuntimeException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not decode encoded word '" + recombineEncodedWord(mimeCharset, encoding, encodedText) + "'", e);
            }
            return null;
        }
    }

    /**
     * Helper method to recombine mimeCharset, encoding and encodedText to
     * an encoded word in the form =?mimeCharset?encoding?encodedText?=
     *
     * @param mimeCharset the charset, e.g. "UTF-8" or "ISO-8859-1"
     * @param encoding    'Q','q' for quoted-printable or 'B','b' for Base64
     * @param encodedText the encoded text
     * @return the decoded string or null on error.
     */
    private static String recombineEncodedWord(String mimeCharset, String encoding, String encodedText) {
        return "=?" + mimeCharset + "?" + encoding + "?" + encodedText + "?=";
    }


    // Replace _ with =20
    private static String replaceUnderscores(String str) {
        // probably faster than String#replace(CharSequence, CharSequence)

        StringBuilder sb = new StringBuilder(128);

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                sb.append("=20");
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}