// import {
//   Table,
//   TableHeader,
//   TableRow,
//   TableHead,
//   TableBody,
//   TableCell,
// } from "@/components/ui/table";
// import { Button } from "@/components/ui/button";
// import { Badge } from "@/components/ui/badge";
// import { Card, CardContent } from "@/components/ui/card";
// import { Pagination } from "@/components/ui/pagination";
// import { Pencil, Trash } from "lucide-react";

// interface Appointment {
//   id: string;
//   petName: string;
//   ownerName: string;
//   specialist: string;
//   status: "Pending" | "Active" | "Completed";
//   dateTime: string;
// }

// const appointmentsData: Appointment[] = [
//   {
//     id: "APT001",
//     petName: "Bella",
//     ownerName: "John Doe",
//     specialist: "Dr. Smith",
//     status: "Pending",
//     dateTime: "2024-02-10 10:00 AM",
//   },
//   {
//     id: "APT002",
//     petName: "Max",
//     ownerName: "Sarah Lee",
//     specialist: "Dr. Brown",
//     status: "Active",
//     dateTime: "2024-02-11 02:00 PM",
//   },
//   {
//     id: "APT003",
//     petName: "Luna",
//     ownerName: "Mike Johnson",
//     specialist: "Dr. Taylor",
//     status: "Completed",
//     dateTime: "2024-02-09 09:00 AM",
//   },
// ];

// const statusColors = {
//   Pending: "yellow",
//   Active: "blue",
//   Completed: "green",
// };

// const Appointments = () => {
//   return (
//     <Card className="mx-auto mt-6 h-full">
//       <CardContent className="pt-6">
//         <h2 className="mb-4 text-2xl font-semibold">Vet Appointments</h2>
//         <Table>
//           <TableHeader>
//             <TableRow>
//               <TableHead>Appointment ID</TableHead>
//               <TableHead>Pet Name</TableHead>
//               <TableHead>Owner Name</TableHead>
//               <TableHead>Specialist</TableHead>
//               <TableHead>Status</TableHead>
//               <TableHead>Date & Time</TableHead>
//               <TableHead>Actions</TableHead>
//             </TableRow>
//           </TableHeader>
//           <TableBody>
//             {appointmentsData.map((appointment) => (
//               <TableRow key={appointment.id}>
//                 <TableCell>{appointment.id}</TableCell>
//                 <TableCell>{appointment.petName}</TableCell>
//                 <TableCell>{appointment.ownerName}</TableCell>
//                 <TableCell>{appointment.specialist}</TableCell>
//                 <TableCell>
//                   <Badge
//                     variant="outline"
//                     className={`bg-${statusColors[appointment.status]}-200 text-${statusColors[appointment.status]}-800`}
//                   >
//                     {appointment.status}
//                   </Badge>
//                 </TableCell>
//                 <TableCell>{appointment.dateTime}</TableCell>
//                 <TableCell className="flex gap-2">
//                   <Button variant="ghost" size="icon">
//                     <Pencil className="h-4 w-4" />
//                   </Button>
//                   <Button variant="destructive" size="icon">
//                     <Trash className="h-4 w-4" />
//                   </Button>
//                 </TableCell>
//               </TableRow>
//             ))}
//           </TableBody>
//         </Table>
//         <div className="mt-4 flex justify-end">
//           <Pagination totalPages={5} currentPage={1} onPageChange={() => {}} />
//         </div>
//       </CardContent>
//     </Card>
//   );
// };

// export default Appointments;
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Pagination } from "@/components/ui/pagination";
import { useState, useEffect } from "react";
import { cn } from "@/lib/utils";

interface Appointment {
  id: string;
  petName: string;
  ownerName: string;
  specialist: string;
  status: "Pending" | "Active" | "Completed" | "Rejected"; // Add Rejected status
  dateTime: string;
  species: string; // Add species
  breed: string; // Add breed
}

const statusColors = {
  Pending: "yellow",
  Active: "blue",
  Completed: "green",
  Rejected: "red", // Color for Rejected
};

const Appointments = ({
  appointmentsData: initialAppointmentsData,
  loading,
  error,
}) => {
  const [appointmentsData, setAppointmentsData] = useState<Appointment[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [appointmentsPerPage, setAppointmentsPerPage] = useState(3);

  useEffect(() => {
    if (initialAppointmentsData) {
      setAppointmentsData(initialAppointmentsData);
      setAppointmentsPerPage(Math.min(3, initialAppointmentsData.length));
    } else if (!loading && !error) {
      const dummyData: Appointment[] = [
        {
          id: "1",
          petName: "Tony",
          ownerName: "Aryan Mehta",
          specialist: "Dr. Vikram Rao",
          status: "Pending",
          dateTime: "2024-03-15 11:00 AM",
          species: "Canine", // Add species to dummy data
          breed: "Labrador", // Add breed to dummy data
        },
        {
          id: "2",
          petName: "keyo",
          ownerName: "Sonia Kapoor",
          specialist: "Dr. Anjali Verma",
          status: "Active",
          dateTime: "2024-03-16 01:00 PM",
          species: "Feline",
          breed: "Siamese",
        },
        {
          id: "3",
          petName: "Mitthu",
          ownerName: "Rahul Singh",
          specialist: "Dr. Priya Nair",
          status: "Completed",
          dateTime: "2024-03-17 03:00 PM",
          species: "Avian",
          breed: "Parrot",
        },
        {
          id: "4",
          petName: "Snake",
          ownerName: "Anjali Joshi",
          specialist: "Dr. Rohan Gupta",
          status: "Rejected",
          dateTime: "2024-03-18 05:00 PM",
          species: "Reptile",
          breed: "Python",
        },
      ];
      setAppointmentsData(dummyData);
      setAppointmentsPerPage(3);
    }
  }, [initialAppointmentsData, loading, error]);

  const indexOfLastAppointment = currentPage * appointmentsPerPage;
  const indexOfFirstAppointment = indexOfLastAppointment - appointmentsPerPage;
  const currentAppointments = appointmentsData.slice(
    indexOfFirstAppointment,
    indexOfLastAppointment,
  );

  const handlePageChange = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  if (loading) {
    return <div>Loading appointments...</div>;
  }

  if (error) {
    return <div>Error loading appointments: {error.message}</div>;
  }

  return (
    <Card className="mx-auto mt-6 h-full">
      <CardContent className="pt-6">
        <h2 className="mb-4 text-2xl font-semibold">Vet Appointments</h2>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Appointment ID</TableHead>
              <TableHead>Pet Name</TableHead>
              <TableHead>Owner Name</TableHead>
              <TableHead>Specialist</TableHead>
              <TableHead>Species</TableHead> {/* Add Species header */}
              <TableHead>Breed</TableHead> {/* Add Breed header */}
              <TableHead>Status</TableHead>
              <TableHead>Date & Time</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {currentAppointments.map((appointment) => (
              <TableRow key={appointment.id}>
                <TableCell>{appointment.id}</TableCell>
                <TableCell>{appointment.petName}</TableCell>
                <TableCell>{appointment.ownerName}</TableCell>
                <TableCell>{appointment.specialist}</TableCell>
                <TableCell>{appointment.species}</TableCell>{" "}
                {/* Add Species cell */}
                <TableCell>{appointment.breed}</TableCell>{" "}
                {/* Add Breed cell */}
                <TableCell>
                  <Badge
                    variant="outline"
                    className={cn(
                      `bg-${statusColors[appointment.status]}-200 text-${statusColors[appointment.status]}-800`,
                    )}
                  >
                    {appointment.status}
                  </Badge>
                </TableCell>
                <TableCell>{appointment.dateTime}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
        <div className="mt-4 flex justify-end">
          <Pagination
            totalPages={Math.ceil(
              appointmentsData.length / appointmentsPerPage,
            )}
            currentPage={currentPage}
            onPageChange={handlePageChange}
          />
        </div>
      </CardContent>
    </Card>
  );
};

export default Appointments;
