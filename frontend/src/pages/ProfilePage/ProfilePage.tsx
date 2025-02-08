
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import {
  PetInfo,
  AccountSettings,
  Appointments,
  LoginAndSecurity,
  Forums,
} from "@/components";

export default function ProfilePage() {
  return (
    <section className="section-width mx-auto flex min-h-screen w-full rounded-lg p-6">
      <Tabs defaultValue="account" className="mt-20 w-full">
        <TabsList className="flex space-x-4 border-b">
          <TabsTrigger
            value="account"
            className="border-b-2 border-red-500 pb-2"
          >
            Account Settings
          </TabsTrigger>
          <TabsTrigger value="petInfo">Pet Info</TabsTrigger>
          <TabsTrigger value="appointments">Appointments</TabsTrigger>
          <TabsTrigger value="loginSecurity">Login & Security</TabsTrigger>
          <TabsTrigger value="forums">Forums</TabsTrigger>
        </TabsList>

        <TabsContent value="account">
          <AccountSettings />
        </TabsContent>

        <TabsContent value="petInfo">
          <PetInfo />
        </TabsContent>

        <TabsContent value="appointments">
          <Appointments />
        </TabsContent>

        <TabsContent value="loginSecurity">
          <LoginAndSecurity />
        </TabsContent>

        <TabsContent value="forums">
          <Forums />
        </TabsContent>
      </Tabs>
    </section>
  );
}
