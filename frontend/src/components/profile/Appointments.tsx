import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Pagination } from "@/components/ui/pagination";
import { Pencil, Trash } from "lucide-react";

interface Appointment {
  id: string;
  petName: string;
  ownerName: string;
  specialist: string;
  status: "Pending" | "Active" | "Completed";
  dateTime: string;
}

const appointmentsData: Appointment[] = [
  {
    id: "APT001",
    petName: "Bella",
    ownerName: "John Doe",
    specialist: "Dr. Smith",
    status: "Pending",
    dateTime: "2024-02-10 10:00 AM",
  },
  {
    id: "APT002",
    petName: "Max",
    ownerName: "Sarah Lee",
    specialist: "Dr. Brown",
    status: "Active",
    dateTime: "2024-02-11 02:00 PM",
  },
  {
    id: "APT003",
    petName: "Luna",
    ownerName: "Mike Johnson",
    specialist: "Dr. Taylor",
    status: "Completed",
    dateTime: "2024-02-09 09:00 AM",
  },
];

const statusColors = {
  Pending: "yellow",
  Active: "blue",
  Completed: "green",
};

const Appointments = () => {
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
              <TableHead>Status</TableHead>
              <TableHead>Date & Time</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {appointmentsData.map((appointment) => (
              <TableRow key={appointment.id}>
                <TableCell>{appointment.id}</TableCell>
                <TableCell>{appointment.petName}</TableCell>
                <TableCell>{appointment.ownerName}</TableCell>
                <TableCell>{appointment.specialist}</TableCell>
                <TableCell>
                  <Badge
                    variant="outline"
                    className={`bg-${statusColors[appointment.status]}-200 text-${statusColors[appointment.status]}-800`}
                  >
                    {appointment.status}
                  </Badge>
                </TableCell>
                <TableCell>{appointment.dateTime}</TableCell>
                <TableCell className="flex gap-2">
                  <Button variant="ghost" size="icon">
                    <Pencil className="h-4 w-4" />
                  </Button>
                  <Button variant="destructive" size="icon">
                    <Trash className="h-4 w-4" />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
        <div className="mt-4 flex justify-end">
          <Pagination totalPages={5} currentPage={1} onPageChange={() => {}} />
        </div>
      </CardContent>
    </Card>
  );
};

export default Appointments;
