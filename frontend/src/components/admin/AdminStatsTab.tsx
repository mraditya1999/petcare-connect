import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { customFetch } from "@/utils/customFetch";
import { ApiResponse } from "@/types/api";

type AdminDashboardDTO = {
  totalUsers: number;
  totalSpecialists: number;
  totalPets: number;
  totalAppointments: number;
  upcomingAppointments: number;
  scheduledAppointments: number;
  completedAppointments: number;
  cancelledAppointments: number;
  verifiedUsers: number;
};

const AdminStatsTab = () => {
  const [data, setData] = useState<AdminDashboardDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadDashboard = async () => {
      setLoading(true);
      setError(null);
      try {
        const response =
          await customFetch.get<ApiResponse<AdminDashboardDTO>>(
            "/dashboard/admin",
          );
        setData(response.data.data);
      } catch (err) {
        setError("Unable to load dashboard stats. Please try again.");
        console.error("Admin dashboard error", err);
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  const stats = data
    ? [
        { label: "Total Users", value: data.totalUsers },
        { label: "Total Specialists", value: data.totalSpecialists },
        { label: "Total Pets", value: data.totalPets },
        { label: "Total Appointments", value: data.totalAppointments },
        { label: "Upcoming Appointments", value: data.upcomingAppointments },
        { label: "Scheduled Appointments", value: data.scheduledAppointments },
        { label: "Completed Appointments", value: data.completedAppointments },
        { label: "Cancelled Appointments", value: data.cancelledAppointments },
        { label: "Verified Users", value: data.verifiedUsers },
      ]
    : [];

  return (
    <>
      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, index) => (
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
    </>
  );
};

export default AdminStatsTab;
