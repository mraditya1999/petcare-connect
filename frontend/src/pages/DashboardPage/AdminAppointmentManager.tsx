import { useEffect, useState } from "react";
import { customFetch } from "@/utils/customFetch";
import { ApiResponse } from "@/types/api";
import { Appointment } from "@/types/appointment-types";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

const AdminAppointmentManager = () => {
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
    if (!id) return;
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
    void loadAppointments();
  }, []);

  return (
    <section className="section-width mx-auto min-h-screen py-8">
      <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="text-3xl font-bold">Admin Appointment Manager</h2>
          <p className="text-sm text-muted-foreground">
            View and delete any appointment on the platform.
          </p>
        </div>
        <button
          onClick={loadAppointments}
          className="rounded-full bg-primary px-4 py-2 text-white"
        >
          Refresh
        </button>
      </div>

      {loading ? (
        <div className="text-center">Loading...</div>
      ) : error ? (
        <div className="rounded-lg border border-red-300 bg-red-50 p-4 text-red-700">
          {error}
        </div>
      ) : (
        <div className="space-y-3">
          {appointments.length === 0 ? (
            <div>No appointments found.</div>
          ) : (
            appointments.map((appointment) => (
              <Card key={appointment.appointmentId}>
                <CardHeader>
                  <CardTitle>
                    #{appointment.appointmentId} - {appointment.status}
                  </CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-2">
                  <p>Pet Owner: {appointment.petOwnerName}</p>
                  <p>Specialist: {appointment.specialistName}</p>
                  <p>Pet: {appointment.petName}</p>
                  <p>Date: {new Date(appointment.date).toLocaleString()}</p>
                  <div className="flex gap-2">
                    <Button
                      variant="destructive"
                      onClick={() =>
                        void deleteAppointment(appointment.appointmentId)
                      }
                    >
                      Delete
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))
          )}
        </div>
      )}
    </section>
  );
};

export default AdminAppointmentManager;
