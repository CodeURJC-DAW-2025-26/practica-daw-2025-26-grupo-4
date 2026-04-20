import type { Route } from "./+types/login";
import { AuthPage } from "~/components/auth-page";

export function links(): Route.LinkDescriptors {
  return [
    { rel: "stylesheet", href: "https://fonts.googleapis.com/css2?family=Roboto:wght@400;500&display=swap" },
    { rel: "stylesheet", href: "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" }
  ];
}

export default function LoginPage() {
  return <AuthPage initialMode="login" />;
}
