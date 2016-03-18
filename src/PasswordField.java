import static com.polaris.engine.render.OpenGL.glBegin;
import static com.polaris.engine.render.OpenGL.glBlend;
import static com.polaris.engine.render.OpenGL.glVertex;
import static org.lwjgl.opengl.GL11.glEnd;

import com.polaris.engine.gui.element.TextField;
import com.polaris.engine.util.MathHelper;

public class PasswordField extends TextField
{
	
	private double initTicks = 0;
	private double highlightTicks = 0;
	private double[] gradient = new double[24];

	public PasswordField()
	{
		super(740, 0, 480, 30);
	}
	
	public void render(double delta)
	{
		initTicks = MathHelper.getExpValue(initTicks, 1, .2, delta);
		highlightTicks = MathHelper.getExpValue(highlightTicks, .3d - (highlighted ? 0 : .3d), .25d, delta);
		int i;
		int j;
		double changeWidth = elementWidth * 2 / gradient.length;
		double x;
		double y;
		double x1;
		double y1;
		for(i = 0; i < gradient.length; i += 2)
		{
			if(MathHelper.isEqual(gradient[i], gradient[i + 1], .001))
			{
				gradient[i] = MathHelper.random(.4) + .2;
			}
			gradient[i + 1] = MathHelper.getLinearValue(gradient[i + 1], gradient[i], .2, delta);
		}
		
		
		glBlend();
		glBegin();
		for(i = 1, j = 0; i < gradient.length - 2; i += 2)
		{
			x = position.x + changeWidth * j;
			y = position.y + elementHeight / 3d;
			x1 = position.x + changeWidth * (j + MathHelper.clamp(0, 2d / gradient.length, initTicks - ((double) i / gradient.length)) * gradient.length / 2);
			y1 = position.y + elementHeight;
			glVertex(x, y, 1, 1, 1, 1, 0);
			glVertex(x, y1, 1, 1, 1, 1, gradient[i] + highlightTicks);
			glVertex(x1, y1, 1, 1, 1, 1, gradient[i + 2] + highlightTicks);
			glVertex(x1, y, 1, 1, 1, 1, 0);
			j++;
		}
		glEnd();
	}
	
	public void update(double delta)
	{
		super.update(delta);
		position.y = ((GuiLogin)gui).arrowYCoord;
	}

}
