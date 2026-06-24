import { useEffect, useRef } from "react";
import { useAppSelector } from "@/app/hooks";
import { useToast } from "@/components/ui/use-toast";
import { customFetch } from "@/utils/customFetch";
import { ROUTES } from "@/utils/constants";
import {
  IFetchNotificationsResponse,
  NotificationItem,
} from "@/types/notification-types";

type NotificationStatus = NotificationItem["status"];

type NotificationStatusMap = Record<number, NotificationStatus>;

const POLL_INTERVAL_MS = 15000;

const NotificationListener = () => {
  const toast = useToast().toast;
  const user = useAppSelector((state) => state.auth.user);
  const storageRef = useRef<NotificationStatusMap>({});
  const isInitialRef = useRef(true);

  useEffect(() => {
    if (!user?.data) return;

    const notify = (
      message: string,
      description: string,
      variant: "default" | "destructive" = "default",
    ) => {
      toast({
        title: message,
        description,
        variant,
        duration: 5000,
      });
    };

    const fetchNotifications = async () => {
      try {
        const response = await customFetch.get<IFetchNotificationsResponse>(
          `${ROUTES.NOTIFICATIONS}?limit=50`,
        );

        const notifications = response.data.data || [];

        if (isInitialRef.current) {
          storageRef.current = Object.fromEntries(
            notifications.map((item) => [item.appointmentId, item.status]),
          );
          isInitialRef.current = false;
          return;
        }

        const knownStatus = storageRef.current;

        notifications.forEach((notification) => {
          const existingStatus = knownStatus[notification.appointmentId];

          if (existingStatus === undefined) {
            notify(
              "New appointment detected",
              `New ${notification.status.toLowerCase()} appointment for ${notification.petName} with ${notification.specialistName}`,
            );
            knownStatus[notification.appointmentId] = notification.status;
            return;
          }

          if (existingStatus !== notification.status) {
            const title =
              notification.status === "COMPLETED"
                ? "Appointment completed"
                : notification.status === "CANCELLED"
                  ? "Appointment cancelled"
                  : "Appointment updated";

            const description = `Appointment for ${notification.petName} with ${notification.specialistName} is now ${notification.status.toLowerCase()}.`;
            const variant =
              notification.status === "CANCELLED" ? "destructive" : "default";

            notify(title, description, variant);
            knownStatus[notification.appointmentId] = notification.status;
          }
        });

        storageRef.current = knownStatus;
      } catch (error) {
        // Fail silently to avoid noisy errors on transient network issues.
        console.debug("Notification polling failed", error);
      }
    };

    // start polling immediately, then at interval
    fetchNotifications();
    const intervalId = setInterval(fetchNotifications, POLL_INTERVAL_MS);

    return () => {
      clearInterval(intervalId);
      isInitialRef.current = true;
      storageRef.current = {};
    };
  }, [toast, user]);

  return null;
};

export default NotificationListener;
