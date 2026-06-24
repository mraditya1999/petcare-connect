import { useEffect, useState } from "react";
import { customFetch } from "@/utils/customFetch";
import {
  fetchExternalCities,
  fetchExternalCountries,
  fetchExternalPincodes,
  fetchExternalStates,
} from "@/utils/locationApi";
import { ApiResponse } from "@/types/api";
import {
  createSpecialistSchema,
  CreateSpecialistInput,
} from "@/utils/validations";
import { handleError } from "@/utils/helpers";
import { ZodError } from "zod";
import ShowToast from "@/components/shared/ShowToast";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

interface SpecialityItem {
  id: number;
  name: string;
  description?: string;
}

const AdminSpecialistTab = () => {
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
  const [specialities, setSpecialities] = useState<string[]>([]);
  const [existingSpecialities, setExistingSpecialities] = useState<
    SpecialityItem[]
  >([]);
  const [countryOptions, setCountryOptions] = useState<string[]>([]);
  const [stateOptions, setStateOptions] = useState<string[]>([]);
  const [cityOptions, setCityOptions] = useState<string[]>([]);
  const [pincodeOptions, setPincodeOptions] = useState<string[]>([]);
  const [formErrors, setFormErrors] = useState<
    Partial<Record<keyof CreateSpecialistInput, string>>
  >({});

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >,
  ) => {
    const { name, value } = e.target;
    if (name === "country") {
      setForm((prev) => ({
        ...prev,
        country: value,
        state: "",
        city: "",
        pincode: "",
      }));
    } else if (name === "state") {
      setForm((prev) => ({ ...prev, state: value, city: "", pincode: "" }));
    } else if (name === "city") {
      setForm((prev) => ({ ...prev, city: value, pincode: "" }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }

    if (formErrors[name as keyof CreateSpecialistInput]) {
      setFormErrors((prev) => ({ ...prev, [name]: undefined }));
    }
  };

  useEffect(() => {
    const fetchSelectData = async () => {
      try {
        const response =
          await customFetch.get<ApiResponse<SpecialityItem[]>>("/specialities");
        const list = response.data?.data || [];
        setSpecialities(list.map((item) => item.name));
        setExistingSpecialities(list);
      } catch (err) {
        console.error("Failed to fetch specialities", err);
      }
    };

    const fetchCountries = async () => {
      try {
        const countries = await fetchExternalCountries();
        setCountryOptions(countries);
      } catch (err) {
        console.error("Failed to fetch countries from external API", err);
      }
    };

    fetchSelectData();
    fetchCountries();
  }, []);

  useEffect(() => {
    if (!form.country) {
      setStateOptions([]);
      setCityOptions([]);
      setPincodeOptions([]);
      return;
    }

    const fetchStates = async () => {
      try {
        const states = await fetchExternalStates(form.country);
        setStateOptions(states);
      } catch (err) {
        console.error("Failed to fetch states from external API", err);
        setStateOptions([]);
      }
    };

    fetchStates();
  }, [form.country]);

  useEffect(() => {
    if (!form.country || !form.state) {
      setCityOptions([]);
      setPincodeOptions([]);
      return;
    }

    const fetchCities = async () => {
      try {
        const cities = await fetchExternalCities(form.country, form.state);
        setCityOptions(cities);
      } catch (err) {
        console.error("Failed to fetch cities from external API", err);
        setCityOptions([]);
      }
    };

    fetchCities();
  }, [form.country, form.state]);

  useEffect(() => {
    if (!form.country || !form.state || !form.city) {
      setPincodeOptions([]);
      return;
    }

    const fetchPincodes = async () => {
      try {
        const pincodes = await fetchExternalPincodes(
          form.country,
          form.state,
          form.city,
        );
        setPincodeOptions(pincodes);
      } catch (err) {
        console.error("Failed to fetch pincodes from external API", err);
        setPincodeOptions([]);
      }
    };

    fetchPincodes();
  }, [form.country, form.state, form.city]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    setLoading(true);
    setError(null);
    setSuccess(null);
    setFormErrors({});

    try {
      const parsedData: CreateSpecialistInput =
        createSpecialistSchema.parse(form);

      const formData = new FormData();
      Object.entries(parsedData).forEach(([key, value]) => {
        formData.append(key, value as string);
      });

      await customFetch.post("/admin/specialists", formData);
      const successMessage = "Specialist created successfully";
      setSuccess(successMessage);
      ShowToast({ description: successMessage, type: "success" });
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
      await refreshSpecialities();
    } catch (err: unknown) {
      const errorMessage = handleError(err);
      setError(errorMessage);
      ShowToast({ description: errorMessage, type: "error" });
      if (err instanceof ZodError) {
        const newErrors: Partial<Record<keyof CreateSpecialistInput, string>> =
          {};
        err.issues?.forEach((issue) => {
          const path = issue.path[0] as keyof CreateSpecialistInput;
          if (path) newErrors[path] = issue.message;
        });
        setFormErrors(newErrors);
      }
      console.error("Admin specialist creation error", err);
    } finally {
      setLoading(false);
    }
  };

  const refreshSpecialities = async () => {
    try {
      const response =
        await customFetch.get<ApiResponse<SpecialityItem[]>>("/specialities");
      const list = response.data?.data || [];
      setExistingSpecialities(list);
      setSpecialities(list.map((item) => item.name));
    } catch (err) {
      console.error("Failed to refresh specialities", err);
    }
  };

  const handleDeleteSpeciality = async (id: number) => {
    setLoading(true);
    try {
      await customFetch.delete(`/specialities/${id}`);
      ShowToast({ description: "Speciality deleted", type: "success" });
      await refreshSpecialities();
    } catch (err) {
      const errorMessage = handleError(err);
      setError(errorMessage);
      ShowToast({ description: errorMessage, type: "error" });
      console.error("Speciality deletion failed", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="mt-4">
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
          <div>
            <Input
              type="text"
              placeholder="First Name"
              name="firstName"
              value={form.firstName}
              onChange={handleChange}
              required
            />
            {formErrors.firstName && (
              <p className="mt-1 text-sm text-red-500">
                {formErrors.firstName}
              </p>
            )}
          </div>
          <div>
            <Input
              type="text"
              placeholder="Last Name"
              name="lastName"
              value={form.lastName}
              onChange={handleChange}
              required
            />
            {formErrors.lastName && (
              <p className="mt-1 text-sm text-red-500">{formErrors.lastName}</p>
            )}
          </div>
          <div>
            <Input
              type="email"
              placeholder="Email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
            />
            {formErrors.email && (
              <p className="mt-1 text-sm text-red-500">{formErrors.email}</p>
            )}
          </div>
          <div>
            <Input
              type="password"
              placeholder="Password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
            />
            {formErrors.password && (
              <p className="mt-1 text-sm text-red-500">{formErrors.password}</p>
            )}
          </div>
          <div>
            <Input
              type="text"
              placeholder="Mobile Number"
              name="mobileNumber"
              value={form.mobileNumber}
              onChange={handleChange}
              required
            />
            {formErrors.mobileNumber && (
              <p className="mt-1 text-sm text-red-500">
                {formErrors.mobileNumber}
              </p>
            )}
          </div>
          <div>
            <label htmlFor="speciality" className="sr-only">
              Speciality
            </label>
            <select
              id="speciality"
              name="speciality"
              value={form.speciality}
              onChange={handleChange}
              className="w-full rounded-md border border-gray-300 px-2 py-1"
              required
            >
              <option value="">Select Speciality</option>
              {specialities.map((speciality) => (
                <option key={speciality} value={speciality}>
                  {speciality}
                </option>
              ))}
            </select>
            {formErrors.speciality && (
              <p className="mt-1 text-sm text-red-500">
                {formErrors.speciality}
              </p>
            )}
          </div>
          <div>
            <label htmlFor="country" className="sr-only">
              Country
            </label>
            <select
              id="country"
              name="country"
              value={form.country}
              onChange={handleChange}
              className="w-full rounded-md border border-gray-300 px-2 py-1"
              required
            >
              <option value="">Select Country</option>
              {countryOptions.map((country) => (
                <option key={country} value={country}>
                  {country}
                </option>
              ))}
            </select>
            {formErrors.country && (
              <p className="mt-1 text-sm text-red-500">{formErrors.country}</p>
            )}
          </div>
          <div>
            <label htmlFor="state" className="sr-only">
              State
            </label>
            <select
              id="state"
              name="state"
              value={form.state}
              onChange={handleChange}
              className="w-full rounded-md border border-gray-300 px-2 py-1"
              required
              disabled={!form.country}
            >
              <option value="">Select State</option>
              {stateOptions.map((state) => (
                <option key={state} value={state}>
                  {state}
                </option>
              ))}
            </select>
            {formErrors.state && (
              <p className="mt-1 text-sm text-red-500">{formErrors.state}</p>
            )}
          </div>
          <div>
            <label htmlFor="city" className="sr-only">
              City
            </label>
            <select
              id="city"
              name="city"
              value={form.city}
              onChange={handleChange}
              className="w-full rounded-md border border-gray-300 px-2 py-1"
              required
              disabled={!form.state}
            >
              <option value="">Select City</option>
              {cityOptions.map((city) => (
                <option key={city} value={city}>
                  {city}
                </option>
              ))}
            </select>
            {formErrors.city && (
              <p className="mt-1 text-sm text-red-500">{formErrors.city}</p>
            )}
          </div>
          <div>
            <label htmlFor="pincode" className="sr-only">
              Pincode
            </label>
            <div className="flex gap-2">
              <select
                id="pincode"
                name="pincode"
                value={form.pincode}
                onChange={handleChange}
                className="w-full rounded-md border border-gray-300 px-2 py-1"
                required
                disabled={!form.city}
              >
                <option value="">Select Pincode</option>
                {pincodeOptions.map((pin) => (
                  <option key={pin} value={pin}>
                    {pin}
                  </option>
                ))}
              </select>
              <Input
                type="text"
                placeholder="Or enter pincode"
                name="pincode"
                value={form.pincode}
                onChange={handleChange}
                disabled={!form.city}
              />
            </div>
            {formErrors.pincode && (
              <p className="mt-1 text-sm text-red-500">{formErrors.pincode}</p>
            )}
          </div>
          <div>
            <Textarea
              placeholder="About"
              name="about"
              value={form.about}
              onChange={handleChange}
              required
            />
            {formErrors.about && (
              <p className="mt-1 text-sm text-red-500">{formErrors.about}</p>
            )}
          </div>
          <div className="flex justify-end md:col-span-2">
            <Button type="submit" disabled={loading}>
              {loading ? "Saving..." : "Create Specialist"}
            </Button>
          </div>
        </form>

        <div className="mt-6">
          <h3 className="text-lg font-semibold">Specialities (Admin)</h3>
          {existingSpecialities.length === 0 ? (
            <p className="mt-2 text-sm text-gray-500">
              No specialities available.
            </p>
          ) : (
            <div className="mt-2 overflow-x-auto">
              <table className="w-full table-auto border-collapse">
                <thead>
                  <tr className="border-b">
                    <th className="p-2 text-left text-sm font-medium">ID</th>
                    <th className="p-2 text-left text-sm font-medium">Name</th>
                    <th className="p-2 text-left text-sm font-medium">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {existingSpecialities.map((item) => (
                    <tr key={item.id} className="border-b">
                      <td className="p-2 text-sm">{item.id}</td>
                      <td className="p-2 text-sm">{item.name}</td>
                      <td className="p-2 text-sm">
                        <Button
                          type="button"
                          variant="destructive"
                          size="sm"
                          onClick={() => handleDeleteSpeciality(item.id)}
                        >
                          Delete
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
};

export default AdminSpecialistTab;
