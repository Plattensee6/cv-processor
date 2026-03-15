import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { fetchApi } from '../../api/client';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Textarea } from '../../components/ui/textarea';
import { 
  Upload,
  FileText,
  CheckCircle2,
  AlertCircle,
  ArrowLeft,
  ArrowRight,
  Plus,
  Trash2,
  Loader2
} from 'lucide-react';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../components/ui/select';
import { Slider } from '../../components/ui/slider';
import { Badge } from '../../components/ui/badge';
import { Separator } from '../../components/ui/separator';
import { useNavigate } from 'react-router';
import { mockCampaigns, type JobRequirement } from '../../data/mockData';

type UploadStatus = 'idle' | 'uploading' | 'processing' | 'success' | 'error';

export function CreateJobAd() {
  const navigate = useNavigate();
  const { token } = useAuth();
  const [currentStep, setCurrentStep] = useState(1);
  const [uploadStatus, setUploadStatus] = useState<UploadStatus>('idle');
  const [fileName, setFileName] = useState('');
  
  // Step 2: Requirements
  const [requirements, setRequirements] = useState<JobRequirement[]>([
    { id: '1', title: 'Java Experience', description: '5+ years of Java development', importance: 90 },
    { id: '2', title: 'Spring Boot', description: 'Strong experience with Spring Boot framework', importance: 85 },
    { id: '3', title: 'Microservices Architecture', description: 'Experience building microservices', importance: 75 },
    { id: '4', title: 'Cloud Experience', description: 'AWS or similar cloud platform', importance: 70 },
    { id: '5', title: 'English Proficiency', description: 'Fluent English communication', importance: 80 },
  ]);

  // Step 3: Job Details
  const [jobTitle, setJobTitle] = useState('');
  const [department, setDepartment] = useState('');
  const [location, setLocation] = useState('');
  const [employmentType, setEmploymentType] = useState('');
  const [campaign, setCampaign] = useState('');

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setFileName(file.name);
      setUploadStatus('uploading');
      
      // Simulate upload and processing
      setTimeout(() => {
        setUploadStatus('processing');
        setTimeout(() => {
          setUploadStatus('success');
        }, 2000);
      }, 1500);
    }
  };

  const updateRequirement = (id: string, field: keyof JobRequirement, value: any) => {
    setRequirements(requirements.map(req => 
      req.id === id ? { ...req, [field]: value } : req
    ));
  };

  const removeRequirement = (id: string) => {
    setRequirements(requirements.filter(req => req.id !== id));
  };

  const addRequirement = () => {
    const newReq: JobRequirement = {
      id: Date.now().toString(),
      title: '',
      description: '',
      importance: 50,
    };
    setRequirements([...requirements, newReq]);
  };

  const canProceedFromStep1 = uploadStatus === 'success';
  const canProceedFromStep2 = requirements.length > 0;
  const canPublish = jobTitle && department && location && employmentType;

  const handlePublish = async () => {
    if (!jobTitle || !location || !employmentType) {
      // minimális validáció – department, campaign csak UI szintű extra
      return;
    }

    try {
      await fetchApi('/admin/jobs', {
        method: 'POST',
        body: JSON.stringify({
          title: jobTitle,
          location,
          employmentType,
          description: '',
          requirements: '',
        }),
        token,
      });
      navigate('/admin/jobs');
    } catch {
      // egyszerű hiba – a designban most nincs külön error surface erre
    }
  };

  const steps = [
    { number: 1, title: 'Upload Job Description' },
    { number: 2, title: 'AI Requirements Review' },
    { number: 3, title: 'Publish Job Ad' },
  ];

  return (
    <div className="p-8 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="icon" onClick={() => navigate('/admin/jobs')}>
          <ArrowLeft className="w-5 h-5" />
        </Button>
        <div>
          <h1 className="text-3xl font-semibold text-gray-900">Create Job Ad</h1>
          <p className="text-sm text-gray-500 mt-1">
            Upload job description and configure requirements
          </p>
        </div>
      </div>

      {/* Progress Steps */}
      <div className="flex items-center justify-between max-w-3xl mx-auto">
        {steps.map((step, index) => (
          <div key={step.number} className="flex items-center flex-1">
            <div className="flex flex-col items-center flex-1">
              <div className={`w-10 h-10 rounded-full flex items-center justify-center border-2 ${
                currentStep > step.number 
                  ? 'bg-blue-600 border-blue-600 text-white'
                  : currentStep === step.number
                  ? 'border-blue-600 text-blue-600'
                  : 'border-gray-300 text-gray-400'
              }`}>
                {currentStep > step.number ? (
                  <CheckCircle2 className="w-5 h-5" />
                ) : (
                  <span className="font-semibold">{step.number}</span>
                )}
              </div>
              <span className={`text-xs mt-2 ${
                currentStep >= step.number ? 'text-gray-900 font-medium' : 'text-gray-400'
              }`}>
                {step.title}
              </span>
            </div>
            {index < steps.length - 1 && (
              <div className={`h-0.5 flex-1 -mt-8 ${
                currentStep > step.number ? 'bg-blue-600' : 'bg-gray-300'
              }`} />
            )}
          </div>
        ))}
      </div>

      {/* Step Content */}
      <div className="max-w-4xl mx-auto">
        {/* Step 1: Upload */}
        {currentStep === 1 && (
          <Card>
            <CardHeader>
              <CardTitle>Upload Job Description</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="border-2 border-dashed border-gray-300 rounded-lg p-12 text-center">
                {uploadStatus === 'idle' && (
                  <div>
                    <Upload className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                    <h3 className="font-medium text-gray-900 mb-2">Upload PDF Job Description</h3>
                    <p className="text-sm text-gray-500 mb-4">
                      AI will automatically extract job requirements
                    </p>
                    <label htmlFor="file-upload">
                      <Button asChild>
                        <span>
                          <Upload className="w-4 h-4 mr-2" />
                          Choose File
                        </span>
                      </Button>
                      <input
                        id="file-upload"
                        type="file"
                        accept=".pdf"
                        className="hidden"
                        onChange={handleFileUpload}
                      />
                    </label>
                  </div>
                )}

                {uploadStatus === 'uploading' && (
                  <div>
                    <Loader2 className="w-12 h-12 text-blue-600 mx-auto mb-4 animate-spin" />
                    <h3 className="font-medium text-gray-900 mb-2">Uploading...</h3>
                    <p className="text-sm text-gray-500">{fileName}</p>
                  </div>
                )}

                {uploadStatus === 'processing' && (
                  <div>
                    <Loader2 className="w-12 h-12 text-blue-600 mx-auto mb-4 animate-spin" />
                    <h3 className="font-medium text-gray-900 mb-2">Processing with AI...</h3>
                    <p className="text-sm text-gray-500">Extracting job requirements</p>
                  </div>
                )}

                {uploadStatus === 'success' && (
                  <div>
                    <CheckCircle2 className="w-12 h-12 text-green-600 mx-auto mb-4" />
                    <h3 className="font-medium text-gray-900 mb-2">Successfully Processed</h3>
                    <p className="text-sm text-gray-500 mb-4">{fileName}</p>
                    <Badge variant="secondary">5 requirements extracted</Badge>
                  </div>
                )}

                {uploadStatus === 'error' && (
                  <div>
                    <AlertCircle className="w-12 h-12 text-red-600 mx-auto mb-4" />
                    <h3 className="font-medium text-gray-900 mb-2">Upload Failed</h3>
                    <p className="text-sm text-gray-500 mb-4">Please try again</p>
                    <Button variant="outline" onClick={() => setUploadStatus('idle')}>
                      Try Again
                    </Button>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        )}

        {/* Step 2: Requirements */}
        {currentStep === 2 && (
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle>AI Extracted Requirements</CardTitle>
                <Button variant="outline" size="sm" onClick={addRequirement}>
                  <Plus className="w-4 h-4 mr-2" />
                  Add Requirement
                </Button>
              </div>
              <p className="text-sm text-gray-500 mt-2">
                Review and adjust the importance of each requirement
              </p>
            </CardHeader>
            <CardContent className="space-y-4">
              {requirements.map((req, index) => (
                <div key={req.id}>
                  {index > 0 && <Separator className="my-4" />}
                  <div className="space-y-4">
                    <div className="flex items-start justify-between gap-4">
                      <div className="flex-1 space-y-3">
                        <div>
                          <Label className="text-xs text-gray-500">Requirement Title</Label>
                          <Input
                            value={req.title}
                            onChange={(e) => updateRequirement(req.id, 'title', e.target.value)}
                            placeholder="e.g., Java Experience"
                            className="mt-1"
                          />
                        </div>
                        <div>
                          <Label className="text-xs text-gray-500">Description</Label>
                          <Textarea
                            value={req.description}
                            onChange={(e) => updateRequirement(req.id, 'description', e.target.value)}
                            placeholder="e.g., 5+ years of Java development"
                            rows={2}
                            className="mt-1"
                          />
                        </div>
                        <div>
                          <Label className="text-xs text-gray-500">
                            Importance: {req.importance}%
                          </Label>
                          <Slider
                            value={[req.importance]}
                            onValueChange={(value) => updateRequirement(req.id, 'importance', value[0])}
                            min={0}
                            max={100}
                            step={5}
                            className="mt-2"
                          />
                        </div>
                      </div>
                      <Button 
                        variant="ghost" 
                        size="icon"
                        onClick={() => removeRequirement(req.id)}
                        className="text-red-600 hover:text-red-700"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        )}

        {/* Step 3: Publish */}
        {currentStep === 3 && (
          <Card>
            <CardHeader>
              <CardTitle>Job Ad Details</CardTitle>
              <p className="text-sm text-gray-500 mt-2">
                Configure job posting details
              </p>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Job Title</Label>
                  <Input
                    value={jobTitle}
                    onChange={(e) => setJobTitle(e.target.value)}
                    placeholder="e.g., Senior Java Developer"
                  />
                </div>

                <div className="space-y-2">
                  <Label>Department</Label>
                  <Select value={department} onValueChange={setDepartment}>
                    <SelectTrigger>
                      <SelectValue placeholder="Select department" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="engineering">Engineering</SelectItem>
                      <SelectItem value="design">Design</SelectItem>
                      <SelectItem value="product">Product</SelectItem>
                      <SelectItem value="marketing">Marketing</SelectItem>
                      <SelectItem value="sales">Sales</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label>Location</Label>
                  <Input
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                    placeholder="e.g., San Francisco, CA"
                  />
                </div>

                <div className="space-y-2">
                  <Label>Employment Type</Label>
                  <Select value={employmentType} onValueChange={setEmploymentType}>
                    <SelectTrigger>
                      <SelectValue placeholder="Select type" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="full-time">Full-time</SelectItem>
                      <SelectItem value="part-time">Part-time</SelectItem>
                      <SelectItem value="contract">Contract</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="col-span-2 space-y-2">
                  <Label>Campaign (Optional)</Label>
                  <Select value={campaign} onValueChange={setCampaign}>
                    <SelectTrigger>
                      <SelectValue placeholder="Select campaign" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="none">No Campaign</SelectItem>
                      {mockCampaigns.map(c => (
                        <SelectItem key={c.id} value={c.id}>
                          {c.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <Separator />

              <div>
                <h4 className="font-medium mb-3">Requirements Summary</h4>
                <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                  <p className="text-sm text-gray-600 mb-3">
                    {requirements.length} requirement(s) configured
                  </p>
                  <div className="space-y-2">
                    {requirements.map(req => (
                      <div key={req.id} className="flex items-center justify-between text-sm">
                        <span className="text-gray-900">{req.title}</span>
                        <Badge variant="outline">{req.importance}%</Badge>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        )}

        {/* Navigation Buttons */}
        <div className="flex items-center justify-between mt-6">
          <Button
            variant="outline"
            onClick={() => setCurrentStep(Math.max(1, currentStep - 1))}
            disabled={currentStep === 1}
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            Previous
          </Button>

          {currentStep < 3 ? (
            <Button
              onClick={() => setCurrentStep(currentStep + 1)}
              disabled={
                (currentStep === 1 && !canProceedFromStep1) ||
                (currentStep === 2 && !canProceedFromStep2)
              }
            >
              Next
              <ArrowRight className="w-4 h-4 ml-2" />
            </Button>
          ) : (
            <Button
              onClick={handlePublish}
              disabled={!canPublish}
            >
              <CheckCircle2 className="w-4 h-4 mr-2" />
              Publish Job Ad
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}
