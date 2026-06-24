import { useEffect, useState } from "react";
import { customFetch } from "@/utils/customFetch";
import { ApiResponse } from "@/types/api";
import { Appointment } from "@/types/appointment-types";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

const AdminAppointmentTab = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadAppointments = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await customFetch.get<
        ApiResponse<{ content: Appointment[] }>
      >("/admin/appointments?page=0&size=100");
      setAppointments(response.data.data.content || []);
    } catch (err) {
      setError("Failed to load appointments");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const deleteAppointment = async (id: number | undefined) => {
    if (!id || !window.confirm("Delete this appointment?")) return;
    try {
      await customFetch.delete<ApiResponse<string>>(
        `/admin/appointments/${id}`,
      );
      setAppointments((prev) =>
        prev.filter((item) => item.appointmentId !== id),
      );
    } catch (err) {
      setError("Failed to delete appointment");
      console.error(err);
    }
  };

  useEffect(() => {
    loadAppointments();
  }, []);

  return (
    <Card className="mt-4">
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>Manage Appointments</CardTitle>
        <Button onClick={loadAppointments} variant="outline">
          Refresh
        </Button>
      </CardHeader>
      <CardContent>
        {error && (
          <div className="mb-4 rounded-lg border border-red-300 bg-red-50 p-3 text-red-700">
            {error}
          </div>
        )}
        {loading ? (
          <div>Loading...</div>
        ) : appointments.length === 0 ? (
          <div className="text-center text-muted-foreground">
            No appointments found
          </div>
        ) : (
          <div className="space-y-3">
            {appointments.map((appointment) => (
              <div
                key={appointment.appointmentId}
                className="flex items-center justify-between rounded border border-gray-200 p-3 dark:border-gray-700"
              >
                <div>
                  <p className="font-semibold">#{appointment.appointmentId}</p>
                  <p className="text-sm">Pet: {appointment.petName}</p>
                  <p className="text-sm text-muted-foreground">
                    Owner: {appointment.petOwnerName}
                  </p>
                  <p className="text-sm text-muted-foreground">
                    Specialist: {appointment.specialistName}
                  </p>
                  <p className="text-xs text-gray-500">
                    {new Date(appointment.date).toLocaleString()}
                  </p>
                  <span className="mt-2 inline-block rounded bg-blue-100 px-2 py-1 text-xs text-blue-800 dark:bg-blue-900 dark:text-blue-200">
                    {appointment.status}
                  </span>
                </div>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => deleteAppointment(appointment.appointmentId)}
                >
                  Delete
                </Button>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default AdminAppointmentTab;
