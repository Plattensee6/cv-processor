import { useState } from 'react';
import { Card, CardContent } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { 
  Search,
  Filter,
  SlidersHorizontal,
  Eye,
  UserCheck,
  UserX,
  MessageSquare
} from 'lucide-react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '../../components/ui/table';
import { Badge } from '../../components/ui/badge';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../components/ui/select';
import { Slider } from '../../components/ui/slider';
import { mockApplications, mockJobAds, type Application } from '../../data/mockData';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '../../components/ui/dialog';
import { Separator } from '../../components/ui/separator';
import { Textarea } from '../../components/ui/textarea';

export function Applications() {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedJob, setSelectedJob] = useState<string>('all');
  const [selectedStatus, setSelectedStatus] = useState<string>('all');
  const [scoreRange, setScoreRange] = useState([0, 100]);
  const [showFilters, setShowFilters] = useState(true);
  const [selectedApplication, setSelectedApplication] = useState<Application | null>(null);

  // Filter applications
  const filteredApplications = mockApplications.filter(app => {
    const matchesSearch = 
      app.candidateName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      app.jobPosition.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesJob = selectedJob === 'all' || app.jobPosition === selectedJob;
    const matchesStatus = selectedStatus === 'all' || app.status === selectedStatus;
    const matchesScore = app.aiMatchScore >= scoreRange[0] && app.aiMatchScore <= scoreRange[1];
    
    return matchesSearch && matchesJob && matchesStatus && matchesScore;
  });

  const getStatusBadge = (status: string) => {
    const variants: Record<string, { variant: "default" | "secondary" | "destructive" | "outline", label: string }> = {
      new: { variant: 'default', label: 'New' },
      shortlisted: { variant: 'secondary', label: 'Shortlisted' },
      rejected: { variant: 'destructive', label: 'Rejected' },
      interviewed: { variant: 'outline', label: 'Interviewed' },
    };
    const config = variants[status] || variants.new;
    return <Badge variant={config.variant}>{config.label}</Badge>;
  };

  const uniqueJobPositions = Array.from(new Set(mockApplications.map(app => app.jobPosition)));

  return (
    <div className="p-8 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold text-gray-900">Applications</h1>
          <p className="text-sm text-gray-500 mt-1">
            Manage and review candidate applications
          </p>
        </div>
        <div className="flex gap-3">
          <Button
            variant={showFilters ? 'default' : 'outline'}
            onClick={() => setShowFilters(!showFilters)}
          >
            <Filter className="w-4 h-4 mr-2" />
            Filters
          </Button>
        </div>
      </div>

      {/* Search and Filters */}
      <Card>
        <CardContent className="pt-6">
          <div className="space-y-4">
            {/* Search Bar */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
              <Input
                placeholder="Search by candidate name or position..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>

            {/* Advanced Filters */}
            {showFilters && (
              <div className="grid grid-cols-4 gap-4 pt-4 border-t">
                <div className="space-y-2">
                  <Label>Job Position</Label>
                  <Select value={selectedJob} onValueChange={setSelectedJob}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">All Positions</SelectItem>
                      {uniqueJobPositions.map(position => (
                        <SelectItem key={position} value={position}>
                          {position}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label>Status</Label>
                  <Select value={selectedStatus} onValueChange={setSelectedStatus}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">All Status</SelectItem>
                      <SelectItem value="new">New</SelectItem>
                      <SelectItem value="shortlisted">Shortlisted</SelectItem>
                      <SelectItem value="interviewed">Interviewed</SelectItem>
                      <SelectItem value="rejected">Rejected</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="col-span-2 space-y-2">
                  <Label>AI Match Score: {scoreRange[0]}% - {scoreRange[1]}%</Label>
                  <div className="pt-2">
                    <Slider
                      value={scoreRange}
                      onValueChange={setScoreRange}
                      min={0}
                      max={100}
                      step={5}
                      className="w-full"
                    />
                  </div>
                </div>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Results Count */}
      <div className="flex items-center justify-between">
        <p className="text-sm text-gray-600">
          Showing <span className="font-medium">{filteredApplications.length}</span> of{' '}
          <span className="font-medium">{mockApplications.length}</span> applications
        </p>
      </div>

      {/* Applications Table */}
      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Candidate Name</TableHead>
                <TableHead>Job Position</TableHead>
                <TableHead>AI Match Score</TableHead>
                <TableHead>Experience</TableHead>
                <TableHead>Key Skills</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Date Applied</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredApplications.map((app) => (
                <TableRow key={app.id}>
                  <TableCell className="font-medium">{app.candidateName}</TableCell>
                  <TableCell className="text-sm text-gray-600">{app.jobPosition}</TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2">
                      <div className="w-16 h-2 bg-gray-100 rounded-full overflow-hidden">
                        <div 
                          className={`h-full rounded-full ${
                            app.aiMatchScore >= 90 ? 'bg-green-600' :
                            app.aiMatchScore >= 80 ? 'bg-blue-600' :
                            app.aiMatchScore >= 70 ? 'bg-yellow-600' :
                            'bg-red-600'
                          }`}
                          style={{ width: `${app.aiMatchScore}%` }}
                        />
                      </div>
                      <span className="text-sm font-medium">{app.aiMatchScore}%</span>
                    </div>
                  </TableCell>
                  <TableCell className="text-sm text-gray-600">{app.experience} years</TableCell>
                  <TableCell>
                    <div className="flex flex-wrap gap-1">
                      {app.keySkills.slice(0, 2).map(skill => (
                        <Badge key={skill} variant="outline" className="text-xs">
                          {skill}
                        </Badge>
                      ))}
                      {app.keySkills.length > 2 && (
                        <Badge variant="outline" className="text-xs">
                          +{app.keySkills.length - 2}
                        </Badge>
                      )}
                    </div>
                  </TableCell>
                  <TableCell>{getStatusBadge(app.status)}</TableCell>
                  <TableCell className="text-sm text-gray-600">
                    {new Date(app.dateApplied).toLocaleDateString('en-US', { 
                      month: 'short', 
                      day: 'numeric',
                      year: 'numeric'
                    })}
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center justify-end gap-2">
                      <Button 
                        variant="ghost" 
                        size="sm"
                        onClick={() => setSelectedApplication(app)}
                      >
                        <Eye className="w-4 h-4" />
                      </Button>
                      <Button variant="ghost" size="sm">
                        <UserCheck className="w-4 h-4" />
                      </Button>
                      <Button variant="ghost" size="sm">
                        <UserX className="w-4 h-4" />
                      </Button>
                      <Button variant="ghost" size="sm">
                        <MessageSquare className="w-4 h-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Candidate Profile Dialog */}
      <Dialog open={!!selectedApplication} onOpenChange={() => setSelectedApplication(null)}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
          {selectedApplication && (
            <>
              <DialogHeader>
                <DialogTitle>Candidate Profile</DialogTitle>
              </DialogHeader>

              <div className="space-y-6">
                {/* Candidate Summary */}
                <div className="flex items-start justify-between">
                  <div>
                    <h3 className="text-xl font-semibold">{selectedApplication.candidateName}</h3>
                    <p className="text-sm text-gray-600 mt-1">
                      Applied for {selectedApplication.jobPosition}
                    </p>
                    <div className="flex items-center gap-4 mt-3 text-sm text-gray-600">
                      <span>{selectedApplication.email}</span>
                      <span>•</span>
                      <span>{selectedApplication.phone}</span>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-3xl font-semibold text-blue-600">
                      {selectedApplication.aiMatchScore}%
                    </div>
                    <p className="text-xs text-gray-500 mt-1">AI Match Score</p>
                  </div>
                </div>

                <Separator />

                {/* Details Grid */}
                <div className="grid grid-cols-2 gap-6">
                  <div>
                    <Label className="text-xs text-gray-500">Experience</Label>
                    <p className="mt-1 font-medium">{selectedApplication.experience} years</p>
                  </div>
                  <div>
                    <Label className="text-xs text-gray-500">Education</Label>
                    <p className="mt-1 font-medium">{selectedApplication.education}</p>
                  </div>
                  <div className="col-span-2">
                    <Label className="text-xs text-gray-500">Key Skills</Label>
                    <div className="flex flex-wrap gap-2 mt-2">
                      {selectedApplication.keySkills.map(skill => (
                        <Badge key={skill} variant="secondary">
                          {skill}
                        </Badge>
                      ))}
                    </div>
                  </div>
                </div>

                <Separator />

                {/* AI Evaluation */}
                <div>
                  <h4 className="font-semibold mb-3">AI Evaluation Summary</h4>
                  <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 space-y-3">
                    <div className="flex items-center justify-between">
                      <span className="text-sm">Technical Skills Match</span>
                      <div className="flex items-center gap-2">
                        <div className="w-24 h-2 bg-blue-100 rounded-full overflow-hidden">
                          <div className="h-full bg-blue-600 rounded-full" style={{ width: '92%' }} />
                        </div>
                        <span className="text-sm font-medium">92%</span>
                      </div>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-sm">Experience Level</span>
                      <div className="flex items-center gap-2">
                        <div className="w-24 h-2 bg-blue-100 rounded-full overflow-hidden">
                          <div className="h-full bg-blue-600 rounded-full" style={{ width: '88%' }} />
                        </div>
                        <span className="text-sm font-medium">88%</span>
                      </div>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-sm">Education Requirements</span>
                      <div className="flex items-center gap-2">
                        <div className="w-24 h-2 bg-blue-100 rounded-full overflow-hidden">
                          <div className="h-full bg-blue-600 rounded-full" style={{ width: '100%' }} />
                        </div>
                        <span className="text-sm font-medium">100%</span>
                      </div>
                    </div>
                  </div>
                </div>

                <Separator />

                {/* HR Notes */}
                <div>
                  <Label className="text-sm font-semibold">HR Notes</Label>
                  <Textarea 
                    placeholder="Add your notes about this candidate..."
                    className="mt-2"
                    rows={4}
                  />
                </div>

                {/* Actions */}
                <div className="flex gap-3">
                  <Button className="flex-1">
                    <UserCheck className="w-4 h-4 mr-2" />
                    Shortlist Candidate
                  </Button>
                  <Button variant="outline" className="flex-1">
                    Schedule Interview
                  </Button>
                  <Button variant="destructive" className="flex-1">
                    <UserX className="w-4 h-4 mr-2" />
                    Reject
                  </Button>
                </div>
              </div>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
