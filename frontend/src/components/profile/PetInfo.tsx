import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { UploadIcon } from "lucide-react";

const PetInfo = () => {
  const [profileImage, setProfileImage] = useState<string | File | null>(null);
  const [petProfile, setPetProfile] = useState({
    petName: "",
    species: "",
    breed: "",
    gender: "",
    weight: "",
    age: "",
  });

  const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      setProfileImage(event.target.files[0]);
    }
  };

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPetProfile({ ...petProfile, [event.target.name]: event.target.value });
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    console.log("Updated Pet Profile: ", petProfile);
  };

  return (
    <Card className="mx-auto mt-6 h-full">
      <CardContent className="pt-6">
        <label className="mx-auto mb-2 flex h-32 w-32 cursor-pointer flex-col items-center justify-center rounded-full border border-dashed">
          {profileImage ? (
            typeof profileImage === "string" ? (
              <img
                src={profileImage}
                alt="Pet Profile"
                className="h-full w-full rounded-lg"
              />
            ) : (
              <img
                src={URL.createObjectURL(profileImage)}
                alt="Pet Profile"
                className="h-full w-full rounded-lg"
              />
            )
          ) : (
            <>
              <UploadIcon className="h-6 w-6 text-gray-400" />
              <span className="text-xs text-gray-500">Upload pet photo</span>
            </>
          )}
          <input type="file" className="hidden" onChange={handleImageChange} />
        </label>

        <form onSubmit={handleSubmit} className="mt-6 grid grid-cols-2 gap-4">
          <div>
            <Label className="mb-2 block text-sm font-medium">Pet Name</Label>
            <Input
              placeholder="Enter your pet's name"
              name="petName"
              value={petProfile.petName}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Species</Label>
            <Input
              placeholder="Enter your pet's species"
              name="species"
              value={petProfile.species}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Breed</Label>
            <Input
              placeholder="Enter your pet's breed"
              name="breed"
              value={petProfile.breed}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Gender</Label>
            <Input
              placeholder="Enter your pet's gender"
              name="gender"
              value={petProfile.gender}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Weight</Label>
            <Input
              placeholder="Enter your pet's weight"
              name="weight"
              value={petProfile.weight}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Age</Label>
            <Input
              placeholder="Enter your pet's age"
              name="age"
              value={petProfile.age}
              onChange={handleChange}
            />
          </div>
          <div className="col-span-2 flex justify-between">
            <Button type="submit" className="px-4 py-2">
              Update Profile
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default PetInfo;
