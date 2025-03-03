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
    <section className="section-width mx-auto min-h-screen w-full rounded-lg p-6">
      <Tabs defaultValue="account" className="mt-20 w-full">
        <TabsList className="hide-scrollbar flex space-x-4 overflow-x-auto border-b md:overflow-x-visible">
          {[
            { value: "account", label: "Account Settings" },
            { value: "petInfo", label: "Pet Info" },
            { value: "appointments", label: "Appointments" },
            { value: "loginSecurity", label: "Login & Security" },
            { value: "forums", label: "Forums" },
          ].map((tab) => (
            <TabsTrigger
              key={tab.value}
              value={tab.value}
              className="flex-shrink-0 pb-2 text-gray-600 transition-all duration-200 data-[state=active]:border-b-2 data-[state=active]:border-red-500 data-[state=active]:text-black"
            >
              {tab.label}
            </TabsTrigger>
          ))}
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
