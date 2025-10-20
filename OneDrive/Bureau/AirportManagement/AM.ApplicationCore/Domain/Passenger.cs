using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AM.ApplicationCore.Domain
{
    public class Passenger
    {
        public int Id { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }

        public string EmailAdress { get; set; }
        public string TelNumber { get; set; }
        public string BirthDate { get; set; }
        public string PassportNumber { get; set; }

        public ICollection<Flight> flights { get; set; } = new List<Flight>();
        public override string ToString()
        {
            return "FirstName" + FirstName + "LastName" + LastName;
        }
    }
}
