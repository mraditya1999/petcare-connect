import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { RootState, AppDispatch } from "@/app/store";
import { fetchAppointments } from "@/features/appointment/appointmentThunk";
import BookingForm from "./components/BookingForm";
import AppointmentDetails from "./components/AppointmentDetails";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

const AppointmentPage = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { appointments, loading, error } = useSelector(
    (state: RootState) => state.appointment,
  );
  const user = useSelector((state: RootState) => state.auth.user);
  const roles = user?.data?.roles || [];
  const isAdmin = roles.includes("ADMIN");
  const isSpecialist = roles.includes("SPECIALIST");

  useEffect(() => {
    dispatch(fetchAppointments({ page: 0, size: 30, admin: isAdmin }));
  }, [dispatch, isAdmin]);

  return (
    <div className="container mx-auto py-8">
      <div
        className={
          isAdmin || isSpecialist
            ? "grid grid-cols-1"
            : "grid grid-cols-1 gap-8 lg:grid-cols-2"
        }
      >
        {!isAdmin && !isSpecialist && (
          <div>
            <Card>
              <CardHeader>
                <CardTitle>Book an Appointment</CardTitle>
              </CardHeader>
              <CardContent>
                <BookingForm />
              </CardContent>
            </Card>
          </div>
        )}
        <div>
          <Card>
            <CardHeader>
              <CardTitle>
                {isAdmin
                  ? "All Appointments"
                  : isSpecialist
                    ? "Specialist Appointments"
                    : "Your Appointments"}
              </CardTitle>
            </CardHeader>
            <CardContent>
              {loading ? (
                <div className="space-y-4">
                  {[...Array(3)].map((_, i) => (
                    <div key={i} className="space-y-2">
                      <Skeleton className="h-4 w-3/4" />
                      <Skeleton className="h-4 w-1/2" />
                      <Skeleton className="h-4 w-1/4" />
                    </div>
                  ))}
                </div>
              ) : error ? (
                <div className="py-4 text-center text-red-500">
                  Error loading appointments: {error}
                </div>
              ) : appointments.length === 0 ? (
                <div className="py-4 text-center text-muted-foreground">
                  No appointments found. Book your first appointment!
                </div>
              ) : (
                <div className="space-y-4">
                  {appointments.map((appointment) => (
                    <AppointmentDetails
                      key={appointment.appointmentId}
                      appointment={appointment}
                    />
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default AppointmentPage;
