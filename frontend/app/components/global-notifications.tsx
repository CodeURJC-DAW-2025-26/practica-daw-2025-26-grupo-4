import { Toast, ToastContainer } from "react-bootstrap";
import { useGlobalNotificationStore } from "~/stores/global-notification-store";

const AUTO_DISMISS_MS = 5000;

const variantToBootstrapBg = {
  danger: "danger",
  success: "success"
} as const;

export function GlobalNotifications() {
  const notifications = useGlobalNotificationStore((state) => state.notifications);
  const removeNotification = useGlobalNotificationStore((state) => state.removeNotification);

  if (notifications.length === 0) {
    return null;
  }

  return (
    <ToastContainer position="top-end" className="global-notifications-container p-3">
      {notifications.map((notification) => (
        <Toast
          key={notification.id}
          autohide
          delay={AUTO_DISMISS_MS}
          onClose={() => removeNotification(notification.id)}
          bg={variantToBootstrapBg[notification.variant]}
          className="border-0 shadow-sm global-notification-toast"
        >
          <Toast.Header closeButton className="justify-content-between">
            <strong className="me-auto">{notification.title}</strong>
          </Toast.Header>
          <Toast.Body className="text-white">{notification.message}</Toast.Body>
        </Toast>
      ))}
    </ToastContainer>
  );
}