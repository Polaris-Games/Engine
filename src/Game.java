import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import com.polaris.engine.Application;
import com.polaris.engine.gamelogic.Axis;
import com.polaris.engine.gamelogic.Rectangle;
import com.polaris.engine.gui.Gui;
import com.polaris.engine.render.Draw;
import com.polaris.engine.render.OpenGL;
import com.polaris.engine.render.Window;
import com.polaris.engine.util.Helper;

public class Game extends Gui
{

	private Rectangle defaultRect = new Rectangle(new Vector2d(100, 100), 100, 100, new Vector3d(0, 0, 0));
	private Rectangle rect = new Rectangle(new Vector2d(0, 0), new Vector2d(200, 200));

	public Game(Application app)
	{
		super(app);
	}

	public void render(double delta)
	{
		Window.gl2d();
		Vector3d position = defaultRect.getPosition();
		GL11.glPushMatrix();
		GL11.glTranslated(position.x, position.y, 0);
		GL11.glRotated(0, 0, 0, 1);
		OpenGL.glBegin();
		if(defaultRect.isSimpleColliding(rect))
		{
			OpenGL.glColor(0, 1, 0, 1);
		}
		else
		{
			OpenGL.glColor(1, 0, 0, 1);
		}
		Draw.rect(- 100, - 100, 100, 100, 0);
		GL11.glEnd();
		GL11.glPopMatrix();
		position = rect.getPosition();
		OpenGL.glBegin();
		List<Axis> axes = new ArrayList<Axis>();
		axes.addAll(Arrays.asList(defaultRect.getAxes()));
		axes.addAll(Arrays.asList(rect.getAxes()));
		boolean flag = true;
		for(Axis axis : axes)
		{
			double overlap = defaultRect.project(axis).getOverlap(rect.project(axis));
			if(overlap < 0)
			{
				flag = false;
			}
		}
		//System.out.println();
		if(flag)
		{
			OpenGL.glColor(0, 0, 1, 1);
		}
		else
		{
			OpenGL.glColor(1, 0, 0, 1);
		}
		Draw.rect(position.x - 100, position.y - 100, position.x + 100, position.y + 100, 0);
		GL11.glEnd();
		rect.getPosition().set(new Vector3d(Application.getMouseX() * (1920d / 1280d), Application.getMouseY() * (1080d / 720d), 0));
	}

}
