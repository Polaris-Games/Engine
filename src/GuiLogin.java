

import com.polaris.engine.Application;
import com.polaris.engine.gui.Gui;
import static com.polaris.engine.render.Draw.*;
import com.polaris.engine.render.FontMap;
import static com.polaris.engine.render.OpenGL.*;
import static com.polaris.engine.render.Texture.*;
import com.polaris.engine.util.Color4d;
import com.polaris.engine.util.MathHelper;
import static org.lwjgl.opengl.GL11.*;

public class GuiLogin extends Gui
{

	public double arrowYCoord = 0;

	public GuiLogin(Application app) 
	{
		super(app);
		this.addElement(new UsernameField());
		this.addElement(new PasswordField());
	}

	public void render(double delta)
	{
		glBlend();
		glColor(new Color4d(240, 188, 17, 255));
		glBegin();
		rect(0, 0, 1920, 1080, -100);
		glColor(new Color4d(137, 105, 0, 100));
		rect(0, 950, 1920, 1080, -100);
		glColor(new Color4d(240, 240, 240, 255));
		rect(810, arrowYCoord - 73, 1110, arrowYCoord - 70, -1);
		rect(810, 1153 - arrowYCoord, 1110, 1150 - arrowYCoord, -1);
		glEnd();

		glEnable(GL_TEXTURE_2D);
		glBindTexture("$login");
		glBindTexture("tripleArrow");
		glBegin();
		rectUV(920, arrowYCoord - 140, 1000, arrowYCoord - 90, -1);
		glEnd();
		glDisable(GL_TEXTURE_2D);
		super.render(delta);
	}

	public void update(double delta)
	{
		super.update(delta);
		arrowYCoord = MathHelper.getExpValue(arrowYCoord, 540, .2, delta);
	}

	public int keyPressed(int keyId, int mods)
	{
		return super.keyPressed(keyId, mods);
	}
}
