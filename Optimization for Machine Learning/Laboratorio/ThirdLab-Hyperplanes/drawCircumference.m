function [] = drawCircumference(x0,R,color)
  theta = 0:2*pi/50:2*pi;
  xP = x0(1)+R*cos(theta);
  yP = x0(2) + R*sin(theta);
  plot(xP,yP,color);
 endfunction
