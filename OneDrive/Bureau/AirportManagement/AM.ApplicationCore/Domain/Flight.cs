using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AM.ApplicationCore.Domain
{
    public class Flight
    {
        public string FlightID { get; set; }

        public string Destination { get; set; }
        public string departure { get; set; }    
        public DateTime FlightDate { get; set; }

        public string EffectiveArrival { get; set; }

        public int EstimatedDuration { get;set; }

        public int PlaneId { get; set; }
        public Plane plane { get; set; }
        public ICollection<Passenger> Passengers { get; set; } = new List<Passenger>();


          
    }
}
