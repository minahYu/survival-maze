import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.ImageIcon;

class GameMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	public String code;
	public String UserName;
	public String data;
	public ImageIcon img;
	public MouseEvent mouse_e;
	public int pen_size; // pen size

	public GameMsg(String UserName, String code, String msg) {
		this.code = code;
		this.UserName = UserName;
		this.data = msg;
	}
}
