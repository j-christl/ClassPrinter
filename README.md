## ClassPrinter

# General
ClassPrinter is a Java Utility that lets you print fields, constructors, methods and inner classes of a class using reflection.

# How to use?
```
public static void printClass(Class<?> clazz, boolean comment)
```
# Example
## Code
```java
printClass(java.awt.Point, true);
```
## Output
```java
/**
 * java.awt.Point
*/

package java.awt;

public class Point extends Point2D implements Serializable {

	/**
	 * 3 Fields
	*/
	public int x = 0;
	public int y = 0;
	private static final long serialVersionUID = -5276940640259749850;

	/**
	 * 3 Constructors
	*/
	public Point(int arg0, int arg1);
	public Point(Point arg0);
	public Point();

	/**
	 * 10 Methods
	*/
	public boolean equals(Object arg0);
	public String toString();
	@Transient
	public Point getLocation();
	public double getX();
	public double getY();
	public void setLocation(Point arg0);
	public void setLocation(int arg0, int arg1);
	public void setLocation(double arg0, double arg1);
	public void move(int arg0, int arg1);
	public void translate(int arg0, int arg1);

	/**
	 * 0 Inner Classes
	*/
}
```
