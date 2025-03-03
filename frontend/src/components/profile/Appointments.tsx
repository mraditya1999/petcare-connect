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
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
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

const Appointments = () => {
  const [appointmentsData, setAppointmentsData] = useState<Appointment[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [appointmentsPerPage, setAppointmentsPerPage] = useState(3);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        // Replace this with your actual data fetching logic
        const dummyData: Appointment[] = [
          {
            id: "1",
            petName: "Tony",
            ownerName: "Aryan Mehta",
            specialist: "Dr. Vikram Rao",
            status: "Pending",
            dateTime: "2024-03-15 11:00 AM",
            species: "Canine",
            breed: "Labrador",
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
        setAppointmentsPerPage(Math.min(3, dummyData.length));
      } catch (error: unknown) {
        if (error instanceof Error) {
          setError(error);
        } else {
          setError(new Error("An unknown error occurred"));
        }
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

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
                <TableCell>{appointment.species}</TableCell>
                <TableCell>{appointment.breed}</TableCell>
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
          <Pagination>
            <PaginationContent>
              <PaginationItem>
                <PaginationPrevious
                  href="#"
                  onClick={() => handlePageChange(currentPage - 1)}
                />
              </PaginationItem>
              {Array.from(
                {
                  length: Math.ceil(
                    appointmentsData.length / appointmentsPerPage,
                  ),
                },
                (_, index) => (
                  <PaginationItem key={index}>
                    <PaginationLink
                      href="#"
                      onClick={() => handlePageChange(index + 1)}
                    >
                      {index + 1}
                    </PaginationLink>
                  </PaginationItem>
                ),
              )}
              <PaginationItem>
                <PaginationNext
                  href="#"
                  onClick={() => handlePageChange(currentPage + 1)}
                />
              </PaginationItem>
            </PaginationContent>
          </Pagination>
        </div>
      </CardContent>
    </Card>
  );
};

export default Appointments;
