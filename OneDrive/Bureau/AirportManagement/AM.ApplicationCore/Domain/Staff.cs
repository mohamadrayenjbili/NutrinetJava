using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AM.ApplicationCore.Domain
{
    public class Staff : Passenger
    {
        public string EmployementDate { get; set; }
        public string Function { get; set; }
        public string salary { get; set; }
    }
}
