import { useEffect, useState } from "react";
import { customFetch } from "@/utils/customFetch";
import { ApiResponse } from "@/types/api";
import { NotificationItem } from "@/types/notification-types";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";

const SpecialistNotificationsPage = () => {
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadNotifications = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await customFetch.get<ApiResponse<NotificationItem[]>>(
          "/notifications?limit=50",
        );
        setNotifications(response.data.data || []);
      } catch (err) {
        console.error("Failed to fetch notifications", err);
        setError("Unable to load notifications at the moment.");
      } finally {
        setLoading(false);
      }
    };

    loadNotifications();
  }, []);

  return (
    <section className="section-width mx-auto min-h-screen py-8">
      <div className="mb-6">
        <h2 className="text-3xl font-bold">Notifications</h2>
        <p className="text-sm text-muted-foreground">
          Live appointment notifications for your account.
        </p>
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : error ? (
        <div className="rounded-lg border border-red-300 bg-red-50 p-4 text-red-700">
          {error}
        </div>
      ) : (
        <div className="space-y-2">
          {notifications.length === 0 ? (
            <div className="rounded-lg border border-gray-300 bg-slate-50 p-4 text-sm">
              There are no notifications yet.
            </div>
          ) : (
            notifications.map((item) => (
              <Card key={item.appointmentId} className="border">
                <CardHeader>
                  <CardTitle>{item.notificationType}</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-sm font-medium">{item.message}</p>
                  <p className="text-xs text-muted-foreground">
                    Appointment #{item.appointmentId} • {item.status} •{" "}
                    {new Date(item.date).toLocaleString()}
                  </p>
                  <p className="text-xs">
                    Pet: {item.petName} • Specialist: {item.specialistName}
                  </p>
                </CardContent>
              </Card>
            ))
          )}
        </div>
      )}
    </section>
  );
};

export default SpecialistNotificationsPage;
