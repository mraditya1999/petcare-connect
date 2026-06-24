import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { customFetch } from "@/utils/customFetch";
import { ApiResponse } from "@/types/api";

type UserDashboardDTO = {
  totalPets: number;
  upcomingAppointments: number;
  completedAppointments: number;
  cancelledAppointments: number;
};

const UserDashboardPage = () => {
  const [data, setData] = useState<UserDashboardDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadDashboard = async () => {
      setLoading(true);
      setError(null);
      try {
        const response =
          await customFetch.get<ApiResponse<UserDashboardDTO>>(
            "/dashboard/user",
          );
        setData(response.data.data);
      } catch (err) {
        setError("Unable to load dashboard stats. Please try again.");
        console.error("User dashboard error", err);
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  const stats = data
    ? [
        { label: "Total Pets", value: data.totalPets },
        { label: "Upcoming Appointments", value: data.upcomingAppointments },
        { label: "Completed Appointments", value: data.completedAppointments },
        { label: "Cancelled Appointments", value: data.cancelledAppointments },
      ]
    : [];

  return (
    <section className="section-width mx-auto min-h-screen py-8">
      <div className="mb-6">
        <h2 className="text-3xl font-bold">User Dashboard</h2>
        <p className="text-sm text-muted-foreground">
          Your pet and appointment summary.
        </p>
      </div>

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 4 }).map((_, index) => (
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

export default UserDashboardPage;
