import { useEffect, useState } from 'react';
import { Card, CardContent } from '../../components/ui/card';
import { fetchApi } from '../../api/client';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../../components/ui/button';
import { 
  Plus,
  Edit,
  Eye,
  Copy,
  XCircle
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
import { mockCampaigns } from '../../data/mockData';
import { Link } from 'react-router';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '../../components/ui/dropdown-menu';

type JobStatus = 'active' | 'closed' | 'draft';

type JobFromApi = {
  id: number;
  title: string;
  location?: string;
  employmentType?: string;
  active: boolean;
  description?: string;
  requirements?: string;
};

type JobRow = {
  id: number;
  title: string;
  department: string;
  location: string;
  campaignName?: string;
  applicationsCount: number;
  status: JobStatus;
  createdDate: string;
};

export function JobAds() {
  const { token } = useAuth();
  const [jobs, setJobs] = useState<JobRow[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const getCampaignName = (campaignId?: string) => {
    if (!campaignId) return '-';
    const campaign = mockCampaigns.find(c => c.id === campaignId);
    return campaign?.name || '-';
  };

  useEffect(() => {
    const loadJobs = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await fetchApi<JobFromApi[]>('/admin/jobs', { token });

        const mapped: JobRow[] = data.map(job => ({
          id: job.id,
          title: job.title,
          department: '—', // backend még nem tartalmaz department mezőt
          location: job.location || '—',
          campaignName: undefined, // később köthető kampányhoz
          applicationsCount: 0, // később köthető jelentkezésekhez
          status: job.active ? 'active' : 'closed',
          createdDate: new Date().toISOString(), // backend jelenleg nem ad createdAt DTO-t
        }));

        setJobs(mapped);
      } catch (e) {
        setError(
          e instanceof Error ? e.message : 'Nem sikerült betölteni az álláshirdetéseket.',
        );
      } finally {
        setLoading(false);
      }
    };

    void loadJobs();
  }, []);

  return (
    <div className="p-8 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold text-gray-900">Job Ads</h1>
          <p className="text-sm text-gray-500 mt-1">
            Manage job postings and requirements
          </p>
        </div>
        <Button asChild>
          <Link to="/admin/jobs/new">
            <Plus className="w-4 h-4 mr-2" />
            Create Job Ad
          </Link>
        </Button>
      </div>

      {/* Job Ads Table */}
      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Job Title</TableHead>
                <TableHead>Department</TableHead>
                <TableHead>Location</TableHead>
                <TableHead>Campaign</TableHead>
                <TableHead>Applications</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Created Date</TableHead>
                <TableHead className="text-right">Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loading && (
                <TableRow>
                  <TableCell colSpan={8} className="text-center text-sm text-gray-500">
                    Betöltés...
                  </TableCell>
                </TableRow>
              )}
              {error && !loading && (
                <TableRow>
                  <TableCell colSpan={8} className="text-center text-sm text-red-500">
                    {error}
                  </TableCell>
                </TableRow>
              )}
              {!loading && !error && jobs.length === 0 && (
                <TableRow>
                  <TableCell colSpan={8} className="text-center text-sm text-gray-500">
                    Még nincs egyetlen álláshirdetés sem.
                  </TableCell>
                </TableRow>
              )}
              {!loading && !error && jobs.map((job) => (
                <TableRow key={job.id}>
                  <TableCell className="font-medium">{job.title}</TableCell>
                  <TableCell className="text-sm text-gray-600">{job.department}</TableCell>
                  <TableCell className="text-sm text-gray-600">{job.location}</TableCell>
                  <TableCell className="text-sm text-gray-600">
                    {job.campaignName ?? getCampaignName(undefined)}
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{job.applicationsCount}</Badge>
                  </TableCell>
                  <TableCell>
                    <Badge variant={
                      job.status === 'active' ? 'default' : 
                      job.status === 'closed' ? 'destructive' : 
                      'secondary'
                    }>
                      {job.status}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-sm text-gray-600">
                    {new Date(job.createdDate).toLocaleDateString('en-US', { 
                      month: 'short', 
                      day: 'numeric',
                      year: 'numeric'
                    })}
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center justify-end gap-2">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="sm">
                            Actions
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem asChild>
                            <Link to={`/admin/jobs/${job.id}`}>
                              <Eye className="w-4 h-4 mr-2" />
                              View Details
                            </Link>
                          </DropdownMenuItem>
                          <DropdownMenuItem asChild>
                            <Link to={`/admin/jobs/${job.id}/edit`}>
                              <Edit className="w-4 h-4 mr-2" />
                              Edit
                            </Link>
                          </DropdownMenuItem>
                          <DropdownMenuItem>
                            <Eye className="w-4 h-4 mr-2" />
                            View Requirements
                          </DropdownMenuItem>
                          <DropdownMenuItem>
                            <Copy className="w-4 h-4 mr-2" />
                            Duplicate
                          </DropdownMenuItem>
                          <DropdownMenuItem className="text-red-600">
                            <XCircle className="w-4 h-4 mr-2" />
                            Close Job
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}
