function [] = drawSphere(x0, R, color)
  % Draws a circle with center x0 and radius R in 2D
  % x0: Center of the circle [x0, y0]
  % R: Radius of the circle
  % color: Line color for the circle

  % Define angles from 0 to 2*pi
  th = 0:pi/50:2*pi;

  % Compute x and y coordinates of the circle
  xUnit = R * cos(th) + x0(1);
  yUnit = R * sin(th) + x0(2);

  % Plot the circle
  plot(xUnit, yUnit, color);

  % Set equal scaling for the axes
  axis equal;

  % Add a legend (optional, can be modified or removed if not needed)
  legend('set A', 'set B', 'S(x0,R)', 'Location', 'northwestoutside');
end

