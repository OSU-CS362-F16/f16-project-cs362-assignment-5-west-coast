package osu.cs362.URLValidator;

public class UrlValidatorExtension extends UrlValidator {

	public static final String[] DEFAULT_SCHEMES = {"http", "https", "ftp"};

	UrlValidatorExtension() {
		super();
	}

	UrlValidatorExtension(long options) {
		super(options);
	}

	UrlValidatorExtension(String[] schemes, RegexValidator authorityValidator, long options) {
		super(schemes, authorityValidator, options);
	}

	UrlValidatorExtension(RegexValidator authorityValidator, long options) {
		super(authorityValidator, options);
	}

	UrlValidatorExtension(String[] schemes, long options) {
		super(schemes, options);
	}

	UrlValidatorExtension(String[] schemes){
		super(schemes);
	}


	@Override
	public boolean isValidScheme(String scheme) {
        //System.out.println("Scheme:" + scheme);
		return(super.isValidScheme(scheme));
	}

	@Override
	public boolean isValidAuthority(String authority) {
        //System.out.println("Authority:" + authority);
		return(super.isValidAuthority(authority));
	}

	@Override
	public boolean isValidPath(String path) {
        //System.out.println("Path:" + path);
		return(super.isValidPath(path));
	}

	@Override
	public boolean isValidQuery(String query) {
        //System.out.println("Query:" + query);
		return(super.isValidQuery(query));
	}

	@Override
	public boolean isValidFragment(String fragment) {
        //System.out.println("Fragment:" + fragment);
		return(super.isValidFragment(fragment));
	}


}
