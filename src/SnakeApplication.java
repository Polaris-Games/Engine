import org.lwjgl.glfw.GLFW;

import com.polaris.engine.Application;
import com.polaris.engine.render.Window;

public class SnakeApplication extends Application
{

	public static void main(String[] args)
	{
		SnakeApplication application = new SnakeApplication();
		application.run();
	}
	
	@Override
	protected void init() 
	{
		setGui(new Game(this));
	}

	@Override
	public long createWindow() 
	{
		Window.setHint(GLFW.GLFW_RESIZABLE, true);
		Window.setHint(GLFW.GLFW_DECORATED, false);
		return Window.createAndCenter(1280, 720, "", 0);
	}

}
