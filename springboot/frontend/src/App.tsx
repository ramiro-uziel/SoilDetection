import { useEffect, useState } from "react";

interface Plant {
  plantId: number;
  plantName: string;
  plantSpecies: string;
  plantLocation: string;
  dateAdded: string;
  notes: string;
}

interface ReadingType {
  readingTypeId: number;
  readingName: string;
  unitOfMeasure: string;
  minHealthyValue: number;
  maxHealthyValue: number;
  description: string;
}

interface SensorReading {
  readingId: number;
  plantId: number;
  readingTypeId: number;
  readingValue: number;
  readingTimestamp: string;
  readingType?: ReadingType;
}

function App() {
  const [activeTab, setActiveTab] = useState<string>("");
  const [plants, setPlants] = useState<Plant[]>([]);
  const [selectedPlant, setSelectedPlant] = useState<Plant | null>(null);
  const [readingData, setReadingData] = useState<{
    [key: string]: SensorReading[];
  }>({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    healthcheck();
    fetchPlants();
  }, []);

  useEffect(() => {
    if (selectedPlant) {
      fetchPlantReadings(selectedPlant.plantId);
    }
  }, [selectedPlant]);

  function healthcheck() {
    fetch("http://localhost:8080/api/healthcheck")
      .then((response) => response.json())
      .then((data) => {
        console.log("Healthcheck response:", data);
      })
      .catch((error) => {
        console.error("Error during healthcheck:", error);
      });
  }

  async function fetchPlants() {
    try {
      setLoading(true);
      const response = await fetch("http://localhost:8080/api/plants");
      const data = await response.json();
      setPlants(data);

      if (data.length > 0) {
        setSelectedPlant(data[0]);
        setActiveTab(data[0].plantName);
      }
      setLoading(false);
    } catch (error) {
      console.error("Error fetching plants:", error);
      setLoading(false);
    }
  }

  async function fetchPlantReadings(plantId: number) {
    try {
      setLoading(true);
      const response = await fetch(
        `http://localhost:8080/api/sensors/plant/${plantId}/readings?limit=20`
      );
      const data = await response.json();
      setReadingData(data);
      setLoading(false);
    } catch (error) {
      console.error(`Error fetching readings for plant ${plantId}:`, error);
      setLoading(false);
    }
  }

  function getLatestReading(readingType: string): number | null {
    if (!readingData[readingType] || readingData[readingType].length === 0) {
      return null;
    }
    return readingData[readingType][0].readingValue;
  }

  function getReadingStatus(readingType: string): string {
    const latestValue = getLatestReading(readingType);
    if (latestValue === null) return "unknown";

    if (
      readingData[readingType].length > 0 &&
      readingData[readingType][0].readingType
    ) {
      const { minHealthyValue, maxHealthyValue } =
        readingData[readingType][0].readingType!;

      if (latestValue < minHealthyValue) return "low";
      if (latestValue > maxHealthyValue) return "high";
      return "normal";
    }

    switch (readingType.toLowerCase()) {
      case "moisture":
        return latestValue < 30 ? "low" : latestValue > 70 ? "high" : "normal";
      case "light":
        return latestValue < 500
          ? "low"
          : latestValue > 10000
          ? "high"
          : "normal";
      case "nutrients":
        return latestValue < 300
          ? "low"
          : latestValue > 1500
          ? "high"
          : "normal";
      case "temperature":
        return latestValue < 15 ? "low" : latestValue > 30 ? "high" : "normal";
      default:
        return "normal";
    }
  }

  function getStatusColor(status: string): string {
    switch (status) {
      case "low":
        return "text-blue-600";
      case "high":
        return "text-red-600";
      case "normal":
        return "text-green-600";
      default:
        return "text-gray-500";
    }
  }

  return (
    <>
      <div className="min-h-screen font-[Geist] p-5">
        <div className="flex flex-col">
          <h1
            className="text-2xl font-extrabold bg-lime-300 p-2 rounded-xl
          border-2 border-black border-dashed"
          >
            SoilDetect
          </h1>

          {loading ? (
            <div className="flex justify-center items-center h-64">
              <p>Cargando datos...</p>
            </div>
          ) : (
            <>
              {/* Tabs */}
              <div className="flex space-x-4 mt-6 mb-4 overflow-x-auto">
                {plants.map((plant) => (
                  <button
                    key={plant.plantId}
                    onClick={() => {
                      setActiveTab(plant.plantName);
                      setSelectedPlant(plant);
                    }}
                    className={`px-4 py-2 rounded-lg border-2 border-black font-bold whitespace-nowrap ${
                      activeTab === plant.plantName
                        ? "bg-lime-300 border-dashed"
                        : "bg-gray-200 border-solid"
                    }`}
                  >
                    {plant.plantName}
                  </button>
                ))}
              </div>

              {selectedPlant && (
                <div className="mb-4">
                  <h2 className="text-xl font-bold">
                    {selectedPlant.plantName}
                  </h2>
                  <p className="text-sm text-gray-600">
                    Especie: {selectedPlant.plantSpecies} | Ubicación:{" "}
                    {selectedPlant.plantLocation}
                  </p>
                  {selectedPlant.notes && (
                    <p className="mt-1 italic text-sm">{selectedPlant.notes}</p>
                  )}
                </div>
              )}

              <div className="grid grid-cols-2 gap-6 flex-grow h-[calc(100vh-270px)]">
                <div className="border-2 border-black border-dashed rounded-xl p-4 bg-blue-100">
                  <h3 className="text-xl font-bold mb-4 text-center">
                    Humedad
                  </h3>
                  <div className="h-48 bg-white rounded border-2 border-gray-300 flex flex-col items-center justify-center p-4">
                    {readingData["Moisture"] ? (
                      <>
                        <span
                          className={`text-3xl font-bold ${getStatusColor(
                            getReadingStatus("Moisture")
                          )}`}
                        >
                          {getLatestReading("Moisture")}
                          {readingData["Moisture"][0]?.readingType
                            ?.unitOfMeasure || "%"}
                        </span>
                        <span className="text-gray-500 mt-2">
                          Última actualización:{" "}
                          {new Date(
                            readingData["Moisture"][0]?.readingTimestamp
                          ).toLocaleString()}
                        </span>
                        <span
                          className={`mt-2 px-3 py-1 rounded-full text-sm ${
                            getReadingStatus("Moisture") === "normal"
                              ? "bg-green-100 text-green-800"
                              : getReadingStatus("Moisture") === "low"
                              ? "bg-blue-100 text-blue-800"
                              : "bg-red-100 text-red-800"
                          }`}
                        >
                          {getReadingStatus("Moisture") === "normal"
                            ? "Óptimo"
                            : getReadingStatus("Moisture") === "low"
                            ? "Necesita Agua"
                            : "Exceso de Agua"}
                        </span>
                      </>
                    ) : (
                      <span className="text-gray-500">
                        No hay datos de humedad disponibles
                      </span>
                    )}
                  </div>
                </div>

                <div className="border-2 border-black border-dashed rounded-xl p-4 bg-yellow-100">
                  <h3 className="text-xl font-bold mb-4 text-center">Luz</h3>
                  <div className="h-48 bg-white rounded border-2 border-gray-300 flex flex-col items-center justify-center p-4">
                    {readingData["Light"] ? (
                      <>
                        <span
                          className={`text-3xl font-bold ${getStatusColor(
                            getReadingStatus("Light")
                          )}`}
                        >
                          {getLatestReading("Light")}
                          {readingData["Light"][0]?.readingType
                            ?.unitOfMeasure || " lux"}
                        </span>
                        <span className="text-gray-500 mt-2">
                          Última actualización:{" "}
                          {new Date(
                            readingData["Light"][0]?.readingTimestamp
                          ).toLocaleString()}
                        </span>
                        <span
                          className={`mt-2 px-3 py-1 rounded-full text-sm ${
                            getReadingStatus("Light") === "normal"
                              ? "bg-green-100 text-green-800"
                              : getReadingStatus("Light") === "low"
                              ? "bg-blue-100 text-blue-800"
                              : "bg-red-100 text-red-800"
                          }`}
                        >
                          {getReadingStatus("Light") === "normal"
                            ? "Luz Óptima"
                            : getReadingStatus("Light") === "low"
                            ? "Necesita Más Luz"
                            : "Demasiada Luz"}
                        </span>
                      </>
                    ) : (
                      <span className="text-gray-500">
                        No hay datos de luz disponibles
                      </span>
                    )}
                  </div>
                </div>

                <div className="border-2 border-black border-dashed rounded-xl p-4 bg-green-100">
                  <h3 className="text-xl font-bold mb-4 text-center">
                    Nutrientes
                  </h3>
                  <div className="h-48 bg-white rounded border-2 border-gray-300 flex flex-col items-center justify-center p-4">
                    {readingData["Nutrients"] ? (
                      <>
                        <span
                          className={`text-3xl font-bold ${getStatusColor(
                            getReadingStatus("Nutrients")
                          )}`}
                        >
                          {getLatestReading("Nutrients")}
                          {readingData["Nutrients"][0]?.readingType
                            ?.unitOfMeasure || " ppm"}
                        </span>
                        <span className="text-gray-500 mt-2">
                          Última actualización:{" "}
                          {new Date(
                            readingData["Nutrients"][0]?.readingTimestamp
                          ).toLocaleString()}
                        </span>
                        <span
                          className={`mt-2 px-3 py-1 rounded-full text-sm ${
                            getReadingStatus("Nutrients") === "normal"
                              ? "bg-green-100 text-green-800"
                              : getReadingStatus("Nutrients") === "low"
                              ? "bg-blue-100 text-blue-800"
                              : "bg-red-100 text-red-800"
                          }`}
                        >
                          {getReadingStatus("Nutrients") === "normal"
                            ? "Nutrientes Óptimos"
                            : getReadingStatus("Nutrients") === "low"
                            ? "Necesita Fertilizante"
                            : "Exceso de Fertilizante"}
                        </span>
                      </>
                    ) : (
                      <span className="text-gray-500">
                        No hay datos de nutrientes disponibles
                      </span>
                    )}
                  </div>
                </div>

                <div className="border-2 border-black border-dashed rounded-xl p-4 bg-red-100">
                  <h3 className="text-xl font-bold mb-4 text-center">
                    Temperatura
                  </h3>
                  <div className="h-48 bg-white rounded border-2 border-gray-300 flex flex-col items-center justify-center p-4">
                    {readingData["Temperature"] ? (
                      <>
                        <span
                          className={`text-3xl font-bold ${getStatusColor(
                            getReadingStatus("Temperature")
                          )}`}
                        >
                          {getLatestReading("Temperature")}
                          {readingData["Temperature"][0]?.readingType
                            ?.unitOfMeasure || "°C"}
                        </span>
                        <span className="text-gray-500 mt-2">
                          Última actualización:{" "}
                          {new Date(
                            readingData["Temperature"][0]?.readingTimestamp
                          ).toLocaleString()}
                        </span>
                        <span
                          className={`mt-2 px-3 py-1 rounded-full text-sm ${
                            getReadingStatus("Temperature") === "normal"
                              ? "bg-green-100 text-green-800"
                              : getReadingStatus("Temperature") === "low"
                              ? "bg-blue-100 text-blue-800"
                              : "bg-red-100 text-red-800"
                          }`}
                        >
                          {getReadingStatus("Temperature") === "normal"
                            ? "Temperatura Óptima"
                            : getReadingStatus("Temperature") === "low"
                            ? "Demasiado Frío"
                            : "Demasiado Calor"}
                        </span>
                      </>
                    ) : (
                      <span className="text-gray-500">
                        No hay datos de temperatura disponibles
                      </span>
                    )}
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </>
  );
}

export default App;
