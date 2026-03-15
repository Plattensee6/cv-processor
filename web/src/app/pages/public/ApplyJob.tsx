import { useState } from 'react';
import { useParams, useNavigate } from 'react-router';
import { getApiUrl } from '../../api/client';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Textarea } from '../../components/ui/textarea';
import { Badge } from '../../components/ui/badge';
import { 
  Upload,
  FileText,
  CheckCircle2,
  AlertCircle,
  ArrowLeft,
  Loader2,
  MapPin,
  Briefcase,
  Clock
} from 'lucide-react';
import { mockJobAds } from '../../data/mockData';

type SubmissionStatus = 'idle' | 'uploading' | 'processing' | 'success' | 'error';

export function ApplyJob() {
  const { jobId } = useParams();
  const navigate = useNavigate();
  
  const job = mockJobAds.find(j => j.id === jobId);
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    yearsExperience: '',
    skills: '',
    education: '',
    coverLetter: '',
  });
  
  const [cvFile, setCvFile] = useState<File | null>(null);
  const [submissionStatus, setSubmissionStatus] = useState<SubmissionStatus>('idle');

  if (!job) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-semibold text-gray-900 mb-2">Job not found</h2>
          <Button onClick={() => navigate('/careers')}>Back to Careers</Button>
        </div>
      </div>
    );
  }

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setCvFile(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!jobId || !cvFile) {
      return;
    }

    setSubmissionStatus('uploading');

    try {
      const form = new FormData();
      form.append('name', `${formData.firstName} ${formData.lastName}`.trim());
      form.append('email', formData.email);
      form.append('phone', formData.phone);
      form.append('cv', cvFile);

      const res = await fetch(getApiUrl(`/jobs/${jobId}/apply`), {
        method: 'POST',
        body: form,
      });
      if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        throw new Error((err as { message?: string }).message || 'Upload failed');
      }

      setSubmissionStatus('success');
    } catch {
      setSubmissionStatus('error');
      setTimeout(() => setSubmissionStatus('idle'), 2000);
    }
  };

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const isFormValid = 
    formData.firstName &&
    formData.lastName &&
    formData.email &&
    formData.phone &&
    cvFile;

  if (submissionStatus === 'success') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <Card className="max-w-md w-full mx-4">
          <CardContent className="pt-12 pb-12 text-center">
            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <CheckCircle2 className="w-8 h-8 text-green-600" />
            </div>
            <h2 className="text-2xl font-semibold text-gray-900 mb-4">
              Application Submitted!
            </h2>
            <p className="text-gray-600 mb-8">
              Thank you for applying to {job.title}. We'll review your application 
              and get back to you soon.
            </p>
            <div className="space-y-3">
              <Button className="w-full" onClick={() => navigate('/careers')}>
                View More Positions
              </Button>
              <Button variant="outline" className="w-full" onClick={() => navigate('/')}>
                Back to Home
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-4xl mx-auto px-6">
        {/* Header */}
        <div className="mb-8">
          <Button 
            variant="ghost" 
            onClick={() => navigate('/careers')}
            className="mb-4"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Careers
          </Button>
          
          <Card>
            <CardHeader>
              <CardTitle className="text-2xl">{job.title}</CardTitle>
              <div className="flex items-center gap-4 text-sm text-gray-600 mt-2">
                <div className="flex items-center gap-1">
                  <Briefcase className="w-4 h-4" />
                  <span>{job.department}</span>
                </div>
                <div className="flex items-center gap-1">
                  <MapPin className="w-4 h-4" />
                  <span>{job.location}</span>
                </div>
                <div className="flex items-center gap-1">
                  <Clock className="w-4 h-4" />
                  <span className="capitalize">{job.employmentType}</span>
                </div>
              </div>
            </CardHeader>
          </Card>
        </div>

        {/* Application Form */}
        <form onSubmit={handleSubmit}>
          <div className="space-y-6">
            {/* Personal Information */}
            <Card>
              <CardHeader>
                <CardTitle>Personal Information</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="firstName">First Name *</Label>
                    <Input
                      id="firstName"
                      value={formData.firstName}
                      onChange={(e) => handleInputChange('firstName', e.target.value)}
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="lastName">Last Name *</Label>
                    <Input
                      id="lastName"
                      value={formData.lastName}
                      onChange={(e) => handleInputChange('lastName', e.target.value)}
                      required
                    />
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="email">Email *</Label>
                    <Input
                      id="email"
                      type="email"
                      value={formData.email}
                      onChange={(e) => handleInputChange('email', e.target.value)}
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="phone">Phone *</Label>
                    <Input
                      id="phone"
                      type="tel"
                      value={formData.phone}
                      onChange={(e) => handleInputChange('phone', e.target.value)}
                      required
                    />
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Professional Information */}
            <Card>
              <CardHeader>
                <CardTitle>Professional Information</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="yearsExperience">Years of Experience</Label>
                  <Input
                    id="yearsExperience"
                    type="number"
                    min="0"
                    value={formData.yearsExperience}
                    onChange={(e) => handleInputChange('yearsExperience', e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="skills">Skills (comma-separated)</Label>
                  <Textarea
                    id="skills"
                    value={formData.skills}
                    onChange={(e) => handleInputChange('skills', e.target.value)}
                    placeholder="e.g., JavaScript, React, TypeScript"
                    rows={3}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="education">Education</Label>
                  <Input
                    id="education"
                    value={formData.education}
                    onChange={(e) => handleInputChange('education', e.target.value)}
                    placeholder="e.g., BSc Computer Science"
                  />
                </div>
              </CardContent>
            </Card>

            {/* CV Upload */}
            <Card>
              <CardHeader>
                <CardTitle>Upload Your CV *</CardTitle>
              </CardHeader>
              <CardContent>
                {!cvFile ? (
                  <div className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center">
                    <Upload className="w-8 h-8 text-gray-400 mx-auto mb-3" />
                    <p className="text-sm text-gray-600 mb-4">
                      Drag and drop your CV here, or click to browse
                    </p>
                    <label htmlFor="cv-upload">
                      <Button type="button" variant="outline" asChild>
                        <span>
                          <FileText className="w-4 h-4 mr-2" />
                          Choose File
                        </span>
                      </Button>
                      <input
                        id="cv-upload"
                        type="file"
                        accept=".pdf,.doc,.docx"
                        className="hidden"
                        onChange={handleFileUpload}
                      />
                    </label>
                    <p className="text-xs text-gray-500 mt-3">
                      Accepted formats: PDF, DOC, DOCX (Max 5MB)
                    </p>
                  </div>
                ) : (
                  <div className="flex items-center justify-between p-4 bg-green-50 border border-green-200 rounded-lg">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                        <FileText className="w-5 h-5 text-green-600" />
                      </div>
                      <div>
                        <p className="font-medium text-sm text-gray-900">{cvFile.name}</p>
                        <p className="text-xs text-gray-500">
                          {(cvFile.size / 1024).toFixed(1)} KB
                        </p>
                      </div>
                    </div>
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      onClick={() => setCvFile(null)}
                    >
                      Remove
                    </Button>
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Cover Letter */}
            <Card>
              <CardHeader>
                <CardTitle>Cover Letter (Optional)</CardTitle>
              </CardHeader>
              <CardContent>
                <Textarea
                  value={formData.coverLetter}
                  onChange={(e) => handleInputChange('coverLetter', e.target.value)}
                  placeholder="Tell us why you're interested in this position..."
                  rows={6}
                />
              </CardContent>
            </Card>

            {/* Position Requirements */}
            <Card>
              <CardHeader>
                <CardTitle>Position Requirements</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {job.requirements.map(req => (
                    <div key={req.id} className="flex items-start gap-3 p-3 bg-gray-50 rounded-lg">
                      <div className="flex-1">
                        <p className="font-medium text-sm text-gray-900">{req.title}</p>
                        <p className="text-xs text-gray-600 mt-1">{req.description}</p>
                      </div>
                      <Badge variant="outline" className="text-xs">
                        {req.importance}%
                      </Badge>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            {/* Submit Button */}
            <div className="flex gap-4">
              <Button
                type="submit"
                disabled={!isFormValid || submissionStatus !== 'idle'}
                className="flex-1"
                size="lg"
              >
                {submissionStatus === 'uploading' && (
                  <>
                    <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                    Uploading CV...
                  </>
                )}
                {submissionStatus === 'processing' && (
                  <>
                    <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                    Processing Application...
                  </>
                )}
                {submissionStatus === 'idle' && 'Submit Application'}
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate('/careers')}
                size="lg"
              >
                Cancel
              </Button>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
