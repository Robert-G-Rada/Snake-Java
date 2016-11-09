package Snake.main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Snake.graphics.Screen;
import Snake.keyboard.Keyboard;

public class Game extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L;
	
	private Thread thread;
	private Screen screen;
	private JFrame frame;
	private BufferedImage image;
	private Random random;
	private Keyboard key;
	
	private boolean running;
	private short direction;
	private boolean[][] map;
	private int[] snake_x;
	private int[] snake_y;
	private int lenght;
	private int apple_x;
	private int apple_y;
	private int FreeSpaces;
	private long lastTime;
	
	private int[] apple_pixels, dot_pixels, head_pixels;
	
	private BufferedImage screenImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)screenImage.getRaster().getDataBuffer()).getData();
	
	private final int SIZE = 600;
	
	public Game()
	{
		Dimension size = new Dimension(SIZE, SIZE);
		setPreferredSize(size);
		
		frame = new JFrame();
		screen = new Screen();
		key = new Keyboard();
		
		addKeyListener(key);
		
		direction = 0;
		map = new boolean[20][20];
		random = new Random();
		snake_x = new int[400];
		snake_y = new int[400];
		apple_pixels = new int[100];
		dot_pixels = new int[100];
		head_pixels = new int[100];
		lenght = 4;
		FreeSpaces = 394;
		for (int i = 0; i < 5; ++i)
		{
			snake_x[i] = 10;
			snake_y[i] = 9 - i;
			map[10][9-i] = true;
		}
		
		gen_apple();
		
		try 
		{
			image = ImageIO.read(Game.class.getResource("/textures/apple.png"));
			int w, h;
			w = image.getWidth();
			h = image.getHeight();
			image.getRGB(0, 0, w, h, apple_pixels, 0, w);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try 
		{
			image = ImageIO.read(Game.class.getResource("/textures/dot.png"));
			int w, h;
			w = image.getWidth();
			h = image.getHeight();
			image.getRGB(0, 0, w, h, dot_pixels, 0, w);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try 
		{
			image = ImageIO.read(Game.class.getResource("/textures/head.png"));
			int w, h;
			w = image.getWidth();
			h = image.getHeight();
			image.getRGB(0, 0, w, h, head_pixels, 0, w);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		lastTime = System.currentTimeMillis();
		long currTime;
		requestFocus();
		while (running)
		{
			currTime = System.currentTimeMillis();
			updateKeys();
			while (currTime - lastTime > 100)
			{
				update();
				if (running)
					render();
				lastTime += 100;
			}
		}
		stopThread();
		System.exit(0);
	}
	
	public void stopThread()
	{
		Thread.currentThread().interrupt();
		return;
	}
	
	public void updateKeys()
	{
		key.update();
		if (key.upOK && direction % 2 == 0)
		{
			direction = 1;
			update();
			render();
			lastTime = System.currentTimeMillis();
			return;
		}
		if (key.downOK && direction % 2 == 0)
		{
			direction = 3;
			update();
			render();
			lastTime = System.currentTimeMillis();
			return;
		}
		if (key.leftOK && (direction % 2 == 1 || direction == 0))
		{
			direction = 4;
			update();
			render();
			lastTime = System.currentTimeMillis();
			return;
		}
		if (key.rightOK && (direction % 2 == 1 || direction == 0))
		{
			direction = 2;
			update();
			render();
			lastTime = System.currentTimeMillis();
			return;
		}
	}
	
	public void update()
	{
		if (direction > 0)
		{
			int lastx, lasty;
			lastx = snake_x[lenght];
			lasty = snake_y[lenght];
			for (int i = lenght; i > 0; --i)
			{
				map[snake_x[i]][snake_y[i]] = false;
				snake_x[i] = snake_x[i-1];
				snake_y[i] = snake_y[i-1];
			}
			
			for (int i = 1; i <= lenght; ++i)
				map[snake_x[i]][snake_y[i]] = true;
			
			if (direction == 1)
				snake_x[0]--;
			if (direction == 2)
				snake_y[0]++;
			if (direction == 3)
				snake_x[0]++;
			if (direction == 4)
				snake_y[0]--;
			
			if (snake_x[0] < 0 || snake_x[0] > 19 || snake_y[0] < 0 || snake_y[0] > 19)
			{
				running = false;
				return;
			}
			if (map[snake_x[0]][snake_y[0]] == true)
			{
				running = false;
				return;
			}
			if (snake_x[0] == apple_x && snake_y[0] == apple_y)
			{
				lenght++;
				FreeSpaces--;
				map[lastx][lasty] = true;
				snake_x[lenght] = lastx;
				snake_y[lenght] = lasty;
				gen_apple();
			}
		}
	}
	
	public void render()
	{
		BufferStrategy bs = getBufferStrategy();
		if (bs == null)
		{
			createBufferStrategy(3);
			return;
		}
		
		screen.Clear();
		screen.render(apple_x, apple_y, snake_x, snake_y, lenght, apple_pixels, dot_pixels, head_pixels);
		
		for (int i = 0; i < 40000; i++)
			pixels[i] = screen.pixels[i];
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(screenImage, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
	
	private synchronized void start()
	{
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}
	
	private synchronized void stop()
	{
		try {
			thread.join();
		} catch (InterruptedException e) {
			    Thread.currentThread().interrupt();
			    return;
		}
	}
	
	private void gen_apple()
	{
		int apple_pos;
		apple_x = 0;
		apple_y = 0;
		apple_pos = random.nextInt(FreeSpaces);
		while (apple_pos > 0)
		{
			if (map[apple_x][apple_y] == false)
				--apple_pos;
			++apple_y;
			if (apple_y == 20)
			{
				++apple_x;
				apple_y = 0;
			}
		}
		while (map[apple_x][apple_y] == true)
		{
			++apple_y;
			if (apple_y == 20)
			{
				++apple_x;
				apple_y = 0;
			}
		}
	}
	
	public static void main(String[] args)
	{
		Game game = new Game();
		
		game.frame.setTitle("Snake");
		game.frame.setResizable(false);
		game.frame.setVisible(true);
		game.frame.add(game);
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		
		game.start();
	}
	
}
