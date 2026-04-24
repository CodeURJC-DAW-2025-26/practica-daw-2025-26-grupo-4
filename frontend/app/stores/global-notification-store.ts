import { create } from "zustand";

export type GlobalNotificationVariant = "danger" | "success";

export type GlobalNotification = {
  id: string;
  variant: GlobalNotificationVariant;
  title: string;
  message: string;
};

interface GlobalNotificationState {
  notifications: GlobalNotification[];
  pushNotification: (notification: Omit<GlobalNotification, "id">) => string;
  removeNotification: (id: string) => void;
}

const createNotificationId = () =>
  `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;

export const useGlobalNotificationStore = create<GlobalNotificationState>((set) => ({
  notifications: [],
  pushNotification: (notification) => {
    const id = createNotificationId();

    set((state) => {
      const duplicated = state.notifications.some(
        (item) => item.variant === notification.variant && item.message === notification.message
      );

      if (duplicated) {
        return state;
      }

      return {
        notifications: [...state.notifications, { id, ...notification }]
      };
    });

    return id;
  },
  removeNotification: (id) =>
    set((state) => ({
      notifications: state.notifications.filter((notification) => notification.id !== id)
    }))
}));

export const notifyError = (message: string) => {
  useGlobalNotificationStore.getState().pushNotification({
    variant: "danger",
    title: "Error",
    message
  });
};

export const notifySuccess = (message: string) => {
  useGlobalNotificationStore.getState().pushNotification({
    variant: "success",
    title: "Correcto",
    message
  });
};