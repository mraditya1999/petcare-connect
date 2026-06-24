import { useState } from "react";
import { customFetch } from "@/utils/customFetch";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

const AdminSpecialistManager = () => {
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    mobileNumber: "",
    speciality: "",
    about: "",
    pincode: "",
    city: "",
    state: "",
    country: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const formData = new FormData();
      Object.entries(form).forEach(([key, value]) => {
        formData.append(key, value as string);
      });

      await customFetch.post("/admin/specialists", formData);
      setSuccess("Specialist created successfully");
      setForm({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        mobileNumber: "",
        speciality: "",
        about: "",
        pincode: "",
        city: "",
        state: "",
        country: "",
      });
    } catch (err: unknown) {
      setError("Failed to create specialist. Check input and try again.");
      console.error("Admin specialist creation error", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="section-width mx-auto min-h-screen py-8">
      <div className="mb-6">
        <h2 className="text-3xl font-bold">Admin Specialist Manager</h2>
        <p className="text-sm text-muted-foreground">
          Admins can create specialists directly. This page also controls
          specialist details.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Create Specialist</CardTitle>
        </CardHeader>
        <CardContent>
          {error && (
            <div className="mb-4 rounded-lg border border-red-300 bg-red-50 p-3 text-red-700">
              {error}
            </div>
          )}
          {success && (
            <div className="mb-4 rounded-lg border border-green-300 bg-green-50 p-3 text-green-700">
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit} className="grid gap-4 md:grid-cols-2">
            <Input
              type="text"
              placeholder="First Name"
              name="firstName"
              value={form.firstName}
              onChange={handleChange}
              required
            />
            <Input
              type="text"
              placeholder="Last Name"
              name="lastName"
              value={form.lastName}
              onChange={handleChange}
              required
            />
            <Input
              type="email"
              placeholder="Email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
            />
            <Input
              type="password"
              placeholder="Password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
            />
            <Input
              type="text"
              placeholder="Mobile Number"
              name="mobileNumber"
              value={form.mobileNumber}
              onChange={handleChange}
              required
            />
            <Input
              type="text"
              placeholder="Speciality"
              name="speciality"
              value={form.speciality}
              onChange={handleChange}
              required
            />
            <Input
              type="text"
              placeholder="Pincode"
              name="pincode"
              value={form.pincode}
              onChange={handleChange}
              required
            />
            <Input
              type="text"
              placeholder="City"
              name="city"
              value={form.city}
              onChange={handleChange}
            />
            <Input
              type="text"
              placeholder="State"
              name="state"
              value={form.state}
              onChange={handleChange}
            />
            <Input
              type="text"
              placeholder="Country"
              name="country"
              value={form.country}
              onChange={handleChange}
            />
            <Textarea
              placeholder="About"
              name="about"
              value={form.about}
              onChange={handleChange}
              required
            />
            <div className="flex justify-end md:col-span-2">
              <Button type="submit" disabled={loading}>
                {loading ? "Saving..." : "Create Specialist"}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </section>
  );
};

export default AdminSpecialistManager;
