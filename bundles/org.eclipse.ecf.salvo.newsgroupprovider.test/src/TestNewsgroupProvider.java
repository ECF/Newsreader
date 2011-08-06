import org.eclipse.ecf.salvo.ui.external.provider.INewsGroupProvider;


public class TestNewsgroupProvider implements INewsGroupProvider {

	public TestNewsgroupProvider() {
	}

	@Override
	public String getUser() {
		return "Foo Bar";
	}

	@Override
	public String getPassword() {
		return "flinder1f7";
	}

	@Override
	public String getOrganization() {
		return "eclipse.org";
	}

	@Override
	public String getLogin() {
		return "exquisitus";
	}

	@Override
	public String getEmail() {
		return "foo.bar@foobar.org";
	}

	@Override
	public String getServerAddress() {
		return "news.eclipse.org";
	}
	
	@Override
	public int getServerPort() {
		return 119;
	}
	
	@Override
	public boolean isSecure() {
		return true;
	}

	@Override
	public String getNewsgroupName() {
		return "eclipse.test";
	}

	@Override
	public String getNewsgroupDescription() {
		return "For testing purposes";
	}

}
