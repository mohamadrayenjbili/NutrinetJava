using AM.ApplicationCore.Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AM.ApplicationCore.Interfaces
{
    public interface IFlightMethods
    {
        List<DateTime> GetFLightDates(string destination);
        void GetFlights(string filterType, string filterValue);
        public void GetFlightDetails(Plane p);
        int GetFlightsNumber(DateTime startDate);



    }
}
