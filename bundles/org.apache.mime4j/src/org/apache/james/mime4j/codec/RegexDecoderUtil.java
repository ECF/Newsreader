package org.apache.james.mime4j.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.util.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexDecoderUtil {
    final static Log log = LogFactory.getLog(RegexDecoderUtil.class);

    final static Pattern regex = Pattern.compile("(.*?)=\\?(.*?)\\?(.*?)\\?(.*?)\\?=", Pattern.DOTALL);
    public static String decodeEncodedWords(String body) {

        StringBuffer sb = new StringBuffer();
        boolean previousWasEncoded = false;

        final Matcher matcher = regex.matcher(body);
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

    private static String decodeEncodedWord(String mimeCharset, String encoding, String encodedText) {
        String charset = CharsetUtil.toJavaCharset(mimeCharset);
        if (charset == null) {
            if (log.isWarnEnabled()) {
                log.warn("MIME charset '" + mimeCharset + "' doesn't have a "
                        + "corresponding Java charset");
            }
            return null;
        } else if (!CharsetUtil.isDecodingSupported(charset)) {
            if (log.isWarnEnabled()) {
                log.warn("Current JDK doesn't support decoding of charset '"
                        + charset + "' (MIME charset '" + mimeCharset
                        + "')");
            }
            return null;
        }

        if (encodedText.length() == 0) {
            if (log.isWarnEnabled()) {
                log.warn("Missing encoded text in encoded word: ");
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
                    log.warn("Warning: Unknown encoding in encoded word ");
                }
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            // should not happen because of isDecodingSupported check above
            if (log.isWarnEnabled()) {
                log.warn("Unsupported encoding in encoded word", e);
            }
            return null;
        } catch (RuntimeException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not decode encoded word", e);
            }
            return null;
        }
    }
}