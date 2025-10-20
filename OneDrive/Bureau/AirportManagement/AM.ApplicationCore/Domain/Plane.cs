using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AM.ApplicationCore.Domain
{
    public enum PlaneType
    {
        boeing,
        airbus
    }
    public class Plane
    {
        public int PlaneId { get; set; }
        public int Capacity { get; set; }
        public DateTime ManufactureDate { get; set; }
        public PlaneType PlaneType { get; set; }

        public ICollection<Flight> Flights { get; set; } = new List<Flight>();

        public override string ToString()
        {
            return "ManufactureDate " + ManufactureDate + "Capacity" + Capacity;
        }
    }
}
