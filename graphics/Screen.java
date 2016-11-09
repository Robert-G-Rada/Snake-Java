package Snake.graphics;

public class Screen 
{
	public int[] pixels;
	
	private int apple_x, apple_y, lenght;
	private int[] snake_x, snake_y, apple_pixels, dot_pixels, head_pixels;
	
	public Screen()
	{
		pixels = new int[40000];
	}
	
	public void Clear()
	{
		for (int i = 0; i < 200; i++)
			for (int j = 0; j < 200; ++j)
				pixels[i * 200 + j] = 0;
	}
	
	public void render(int apple_x, int apple_y, int[] snake_x, int[] snake_y, int lenght, int[] apple_pixels, int[] dot_pixels, int[] head_pixels)
	{
		this.apple_x = apple_x;
		this.apple_y = apple_y;
		this.snake_x = snake_x;
		this.snake_y = snake_y;
		this.lenght = lenght;
		this.head_pixels = head_pixels;
		this.apple_pixels = apple_pixels;
		this.dot_pixels = dot_pixels;
		
		int i, j, ImageCorner_x, ImageCorner_y;
		
		ImageCorner_x = apple_x * 10;
		ImageCorner_y = apple_y * 10;
		
		for (i = 0; i < 10; i++)
			for (j = 0; j < 10; j++)
			{
				pixels[(ImageCorner_x + i) * 200 + (ImageCorner_y + j)] = apple_pixels[i * 10 + j];
			}
		
		ImageCorner_x = snake_x[0] * 10;
		ImageCorner_y = snake_y[0] * 10;
		
		for (i = 0; i < 10; i++)
			for (j = 0; j < 10; j++)
			{
				pixels[(ImageCorner_x + i) * 200 + (ImageCorner_y + j)] = head_pixels[i * 10 + j];
			}
		
		for (int l = 1; l <= lenght; ++l)
		{
			ImageCorner_x = snake_x[l] * 10;
			ImageCorner_y = snake_y[l] * 10;
			
			for (i = 0; i < 10; i++)
				for (j = 0; j < 10; j++)
				{
					pixels[(ImageCorner_x + i) * 200 + (ImageCorner_y + j)] = dot_pixels[i * 10 + j];
				}
		}
		
	}
	
}
