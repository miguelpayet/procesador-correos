package adaptador;

public class AdaptadorException extends Exception {

	@SuppressWarnings("unused")
	public AdaptadorException(String message) {
		super(message);
	}

	@SuppressWarnings("unused")
	public AdaptadorException(String message, Throwable throwable) {
		super(message, throwable);
	}

}