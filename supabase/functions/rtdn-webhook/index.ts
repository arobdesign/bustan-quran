// Real-Time Developer Notifications (RTDN) Pub/Sub push handler
import { serve } from "https://deno.land/std@0.208.0/http/server.ts";

serve(async (req: Request) => {
  if (req.method !== "POST") return new Response("Method Not Allowed", { status: 405 });
  const envelope = await req.json();
  const rawData = envelope?.message?.data;
  if (!rawData) return new Response("Bad Request", { status: 400 });
  const decoded = JSON.parse(atob(rawData));
  // Handle subscriptionNotification (revoked/canceled) & oneTimeProductNotification (refunded)
  console.log("RTDN notificationType received", Boolean(decoded?.subscriptionNotification || decoded?.oneTimeProductNotification));
  return Response.json({ status: "ok" });
});
