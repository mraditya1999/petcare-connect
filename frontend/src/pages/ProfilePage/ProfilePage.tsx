import { useState, memo } from "react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import {
  PetInfo,
  AccountSettings,
  Appointments,
  LoginAndSecurity,
  Forums,
} from "@/components";

// Memoize tab components to prevent unnecessary re-renders
const MemoAccountSettings = memo(AccountSettings);
const MemoPetInfo = memo(PetInfo);
const MemoAppointments = memo(Appointments);
const MemoLoginAndSecurity = memo(LoginAndSecurity);
const MemoForums = memo(Forums);

// Move tabs array outside the component so objects are stable
const PROFILE_TABS = [
  { value: "account", label: "Account Settings" },
  { value: "petInfo", label: "Pet Info" },
  { value: "appointments", label: "Appointments" },
  { value: "loginSecurity", label: "Login & Security" },
  { value: "forums", label: "Forums" },
];

const ProfilePage = () => {
  // Controlled state for tabs
  const [tab, setTab] = useState("account");

  return (
    <section className="section-width mx-auto min-h-screen w-full rounded-lg bg-white p-6 text-gray-900 dark:bg-transparent dark:text-gray-100">
      <Tabs value={tab} onValueChange={setTab} className="mt-20 w-full">
        <TabsList className="hide-scrollbar flex space-x-4 overflow-x-auto border-b border-gray-300 dark:border-gray-700 md:overflow-x-visible">
          {PROFILE_TABS.map((t) => (
            <TabsTrigger
              key={t.value}
              value={t.value}
              className="flex-shrink-0 pb-2 text-gray-600 transition-all duration-200 data-[state=active]:border-b-2 data-[state=active]:border-red-500 data-[state=active]:text-black dark:text-gray-300 dark:data-[state=active]:text-white"
            >
              {t.label}
            </TabsTrigger>
          ))}
        </TabsList>

        <TabsContent value="account">
          <MemoAccountSettings />
        </TabsContent>

        <TabsContent value="petInfo">
          <MemoPetInfo />
        </TabsContent>

        <TabsContent value="appointments">
          <MemoAppointments />
        </TabsContent>

        <TabsContent value="loginSecurity">
          <MemoLoginAndSecurity />
        </TabsContent>

        <TabsContent value="forums">
          <MemoForums />
        </TabsContent>
      </Tabs>
    </section>
  );
};

export default ProfilePage;
