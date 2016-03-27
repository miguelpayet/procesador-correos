package adaptador;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

public abstract class Adaptador {

	private DatabaseMysql database;

	public Adaptador() throws AdaptadorException {
		init();
	}

	protected String extraerTexto(Message message) throws IOException, MessagingException {
		String result = null;
		if (message instanceof MimeMessage) {
			MimeMessage m = (MimeMessage) message;
			Object contentObject = m.getContent();
			if (contentObject instanceof Multipart) {
				BodyPart clearTextPart = null;
				Multipart content = (Multipart) contentObject;
				int count = content.getCount();
				for (int i = 0; i < count; i++) {
					BodyPart part = content.getBodyPart(i);
					if (part.isMimeType("text/plain")) {
						clearTextPart = part;
						break;
					}
				}
				if (clearTextPart != null) {
					result = (String) clearTextPart.getContent();
				}

			} else if (contentObject instanceof String) { // a simple text message
				result = (String) contentObject;
			} else { // not a mime message
				result = null;
			}
		}
		return result;
	}

	abstract void init() throws AdaptadorException;

}
