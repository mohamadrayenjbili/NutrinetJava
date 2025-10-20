// See https://aka.ms/new-console-template for more information
using AM.ApplicationCore.Domain;


Plane plane = new Plane();
plane.Capacity = 100;
plane.ManufactureDate = new DateTime(2025, 09, 15, 13, 10, 20);

PlaneType boeing = PlaneType.boeing;

plane.PlaneType = boeing;  

Plane plane2 = new Plane { Capacity= 100, ManufactureDate=DateTime.Now };

Console.WriteLine(plane2);
Console.WriteLine(plane);