using AM.ApplicationCore.Domain;
using AM.ApplicationCore.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;

namespace AM.ApplicationCore.Services
{
    public class FlightMethods : IFlightMethods
    {
        public List<Flight> Flights { get; set; } = new List<Flight>();

        public List<DateTime> GetFLightDates(string destination)
        {
            // Version LINQ, le code foreach et for reste commenté
            //for (int i = 0; i < Flights.Count; i++)
            //{
            //    if (Flights[i].Destination == destination)
            //    {
            //        flightDates.Add(Flights[i].FlightDate);
            //    }
            //}

            //foreach (var flight in Flights)
            //{
            //    if (flight.Destination == destination)
            //    {
            //        flightDates.Add(flight.FlightDate);
            //    }
            //}

            return Flights
                   .Where(f => f.Destination == destination)
                   .Select(f => f.FlightDate)
                   .ToList();
        }

        public void GetFlightDetails(Plane p)
        {
            // Filtrer les vols correspondant à l'avion passé en paramètre
            var req = from f in Flights
                      where f.plane == p
                      select new { f.FlightDate, f.Destination };

            foreach (var f in req)
            {
                Console.WriteLine(f);
            }
        }


        public int GetFlightsNumber(DateTime startDate)
        {
            DateTime endDate = startDate.AddDays(7);

            // Compter directement avec LINQ query syntax
            return (from f in Flights
                    where f.FlightDate >= startDate && f.FlightDate <= endDate
                    select f).Count();
        }


        public void GetFlights(string filterType, string filterValue)
        {
            switch (filterType)
            {
                case "Destination":
                    foreach (var f in Flights)
                    {
                        if (f.Destination == filterValue)
                            Console.WriteLine(f);
                    }
                    break;

                case "EstimatedDuration":
                    foreach (var f in Flights)
                    {
                        if (f.EstimatedDuration == Int32.Parse(filterValue))
                            Console.WriteLine(f);
                    }
                    break;

                case "FlightDate":
                    foreach (var f in Flights)
                    {
                        if (f.FlightDate.ToString("yyyy-MM-dd") == filterValue)
                            Console.WriteLine(f);
                    }
                    break;

                case "PlaneId":
                    foreach (var f in Flights)
                    {
                        if (f.PlaneId == Int32.Parse(filterValue))
                            Console.WriteLine(f);
                    }
                    break;

                default:
                    Console.WriteLine("Filtre non reconnu !");
                    break;
            }
        }
    }
}
