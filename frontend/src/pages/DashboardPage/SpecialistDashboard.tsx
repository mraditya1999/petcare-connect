import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { customFetch } from "@/utils/customFetch";
import { ROUTES } from "@/utils/constants";
import { ApiResponse } from "@/types/api";

type SpecialistDashboardDTO = {
  totalAppointments: number;
  upcomingAppointments: number;
  completedAppointments: number;
  cancelledAppointments: number;
  averageRating: number;
};

const SpecialistDashboard = () => {
  const [data, setData] = useState<SpecialistDashboardDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadDashboard = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await customFetch.get<
          ApiResponse<SpecialistDashboardDTO>
        >("/dashboard/specialist");
        setData(response.data.data);
      } catch (err) {
        setError("Unable to load dashboard stats. Please try again.");
        console.error("Specialist dashboard error", err);
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  const stats = data
    ? [
        { label: "Total Appointments", value: data.totalAppointments },
        { label: "Upcoming Appointments", value: data.upcomingAppointments },
        { label: "Completed Appointments", value: data.completedAppointments },
        { label: "Cancelled Appointments", value: data.cancelledAppointments },
        { label: "Average Rating", value: data.averageRating.toFixed(2) },
      ]
    : [];

  return (
    <section className="section-width mx-auto min-h-screen py-8">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold">Specialist Dashboard</h2>
          <p className="text-sm text-muted-foreground">
            Your personal appointment and rating statistics.
          </p>
        </div>
        <Link
          to={ROUTES.NOTIFICATIONS}
          className="rounded-md border border-blue-300 bg-blue-50 px-3 py-1 text-sm font-medium text-blue-700 hover:bg-blue-100"
        >
          View Notifications
        </Link>
      </div>

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 5 }).map((_, index) => (
            <Card key={index} className="h-28">
              <CardContent>
                <Skeleton className="h-6 w-2/3" />
                <Skeleton className="mt-3 h-10 w-1/2" />
              </CardContent>
            </Card>
          ))}
        </div>
      ) : error ? (
        <div className="rounded-lg border border-red-300 bg-red-50 p-4 text-red-700">
          {error}
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {stats.map((stat) => (
            <Card key={stat.label}>
              <CardHeader>
                <CardTitle>{stat.value}</CardTitle>
              </CardHeader>
              <CardContent>{stat.label}</CardContent>
            </Card>
          ))}
        </div>
      )}
    </section>
  );
};

export default SpecialistDashboard;
