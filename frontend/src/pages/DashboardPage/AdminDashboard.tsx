import { useState } from "react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import AdminStatsTab from "@/components/admin/AdminStatsTab";
import AdminSpecialistTab from "@/components/admin/AdminSpecialistTab";
import AdminUserTab from "@/components/admin/AdminUserTab";
import AdminForumTab from "@/components/admin/AdminForumTab";
import AdminAppointmentTab from "@/components/admin/AdminAppointmentTab";

const AdminDashboard = () => {
  const [tab, setTab] = useState("overview");

  return (
    <section className="section-width mx-auto min-h-screen py-8">
      <div className="mb-6">
        <h2 className="text-3xl font-bold">Admin Dashboard</h2>
        <p className="text-sm text-muted-foreground">
          Manage all platform resources from one place.
        </p>
      </div>

      <Tabs value={tab} onValueChange={setTab} className="w-full">
        <TabsList className="hide-scrollbar flex space-x-4 overflow-x-auto border-b border-gray-300 dark:border-gray-700 md:overflow-x-visible">
          <TabsTrigger
            value="overview"
            className="flex-shrink-0 pb-2 text-gray-600 transition-all duration-200 data-[state=active]:border-b-2 data-[state=active]:border-red-500 data-[state=active]:text-black dark:text-gray-300 dark:data-[state=active]:text-white"
          >
            Overview
          </TabsTrigger>
          <TabsTrigger
            value="specialists"
            className="flex-shrink-0 pb-2 text-gray-600 transition-all duration-200 data-[state=active]:border-b-2 data-[state=active]:border-red-500 data-[state=active]:text-black dark:text-gray-300 dark:data-[state=active]:text-white"
          >
            Specialists
          </TabsTrigger>
          <TabsTrigger
            value="users"
            className="flex-shrink-0 pb-2 text-gray-600 transition-all duration-200 data-[state=active]:border-b-2 data-[state=active]:border-red-500 data-[state=active]:text-black dark:text-gray-300 dark:data-[state=active]:text-white"
          >
            Users
          </TabsTrigger>
          <TabsTrigger
            value="forums"
            className="flex-shrink-0 pb-2 text-gray-600 transition-all duration-200 data-[state=active]:border-b-2 data-[state=active]:border-red-500 data-[state=active]:text-black dark:text-gray-300 dark:data-[state=active]:text-white"
          >
            Forums
          </TabsTrigger>
          <TabsTrigger
            value="appointments"
            className="flex-shrink-0 pb-2 text-gray-600 transition-all duration-200 data-[state=active]:border-b-2 data-[state=active]:border-red-500 data-[state=active]:text-black dark:text-gray-300 dark:data-[state=active]:text-white"
          >
            Appointments
          </TabsTrigger>
        </TabsList>

        <TabsContent value="overview">
          <AdminStatsTab />
        </TabsContent>

        <TabsContent value="specialists">
          <AdminSpecialistTab />
        </TabsContent>

        <TabsContent value="users">
          <AdminUserTab />
        </TabsContent>

        <TabsContent value="forums">
          <AdminForumTab />
        </TabsContent>

        <TabsContent value="appointments">
          <AdminAppointmentTab />
        </TabsContent>
      </Tabs>
    </section>
  );
};

export default AdminDashboard;
