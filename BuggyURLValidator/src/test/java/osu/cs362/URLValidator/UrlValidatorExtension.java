package osu.cs362.URLValidator;

public class UrlValidatorExtension extends UrlValidator {
	
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
		return(super.isValidScheme(scheme));
	}
	
	@Override
	public boolean isValidAuthority(String authority) {
		return(super.isValidAuthority(authority));
	}
	
	@Override
	public boolean isValidPath(String path) {
		return(super.isValidPath(path));
	}
	
	@Override
	public boolean isValidQuery(String query) {
		return(super.isValidQuery(query));
	}
	
	@Override
	public boolean isValidFragment(String fragment) {
		return(super.isValidFragment(fragment));
	}
	
	
}
