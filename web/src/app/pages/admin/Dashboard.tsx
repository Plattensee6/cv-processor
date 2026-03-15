import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { 
  FileText, 
  UserCheck, 
  UserX, 
  TrendingUp,
  Plus,
  Upload
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
import { mockApplications, mockJobAds } from '../../data/mockData';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';
import { Link } from 'react-router';

export function Dashboard() {
  // Calculate statistics
  const totalApplications = mockApplications.length;
  const newToday = mockApplications.filter(app => app.dateApplied === '2026-03-11').length;
  const shortlisted = mockApplications.filter(app => app.status === 'shortlisted').length;
  const rejected = mockApplications.filter(app => app.status === 'rejected').length;

  // Recent applications
  const recentApplications = [...mockApplications]
    .sort((a, b) => new Date(b.dateApplied).getTime() - new Date(a.dateApplied).getTime())
    .slice(0, 5);

  // AI Score distribution data
  const scoreDistribution = [
    { name: '90-100', value: mockApplications.filter(app => app.aiMatchScore >= 90).length, color: '#10b981' },
    { name: '80-89', value: mockApplications.filter(app => app.aiMatchScore >= 80 && app.aiMatchScore < 90).length, color: '#3b82f6' },
    { name: '70-79', value: mockApplications.filter(app => app.aiMatchScore >= 70 && app.aiMatchScore < 80).length, color: '#f59e0b' },
    { name: '60-69', value: mockApplications.filter(app => app.aiMatchScore < 70).length, color: '#ef4444' },
  ];

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

  return (
    <div className="p-8 space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold text-gray-900">Dashboard</h1>
          <p className="text-sm text-gray-500 mt-1">Welcome back! Here's your recruitment overview.</p>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" asChild>
            <Link to="/admin/jobs/new">
              <Upload className="w-4 h-4 mr-2" />
              Upload Job Description
            </Link>
          </Button>
          <Button asChild>
            <Link to="/admin/jobs/new">
              <Plus className="w-4 h-4 mr-2" />
              Create Job Ad
            </Link>
          </Button>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-4 gap-6">
        <Card>
          <CardHeader className="pb-3">
            <div className="flex items-center justify-between">
              <CardTitle className="text-sm font-medium text-gray-600">Total Applications</CardTitle>
              <FileText className="w-4 h-4 text-gray-400" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-semibold text-gray-900">{totalApplications}</div>
            <p className="text-xs text-gray-500 mt-1">Across all positions</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <div className="flex items-center justify-between">
              <CardTitle className="text-sm font-medium text-gray-600">New Today</CardTitle>
              <TrendingUp className="w-4 h-4 text-blue-500" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-semibold text-gray-900">{newToday}</div>
            <p className="text-xs text-green-600 mt-1">+2 from yesterday</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <div className="flex items-center justify-between">
              <CardTitle className="text-sm font-medium text-gray-600">Shortlisted</CardTitle>
              <UserCheck className="w-4 h-4 text-green-500" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-semibold text-gray-900">{shortlisted}</div>
            <p className="text-xs text-gray-500 mt-1">{Math.round((shortlisted / totalApplications) * 100)}% of total</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-3">
            <div className="flex items-center justify-between">
              <CardTitle className="text-sm font-medium text-gray-600">Rejected</CardTitle>
              <UserX className="w-4 h-4 text-red-500" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-semibold text-gray-900">{rejected}</div>
            <p className="text-xs text-gray-500 mt-1">{Math.round((rejected / totalApplications) * 100)}% of total</p>
          </CardContent>
        </Card>
      </div>

      {/* Content Grid */}
      <div className="grid grid-cols-3 gap-6">
        {/* Recent Applications */}
        <Card className="col-span-2">
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle>Recent Applications</CardTitle>
              <Button variant="ghost" size="sm" asChild>
                <Link to="/admin/applications">View All</Link>
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Candidate</TableHead>
                  <TableHead>Position</TableHead>
                  <TableHead>AI Match</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Date</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {recentApplications.map((app) => (
                  <TableRow key={app.id}>
                    <TableCell className="font-medium">{app.candidateName}</TableCell>
                    <TableCell className="text-sm text-gray-600">{app.jobPosition}</TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2">
                        <div className="w-12 h-2 bg-gray-100 rounded-full overflow-hidden">
                          <div 
                            className="h-full bg-blue-600 rounded-full" 
                            style={{ width: `${app.aiMatchScore}%` }}
                          />
                        </div>
                        <span className="text-sm font-medium">{app.aiMatchScore}%</span>
                      </div>
                    </TableCell>
                    <TableCell>{getStatusBadge(app.status)}</TableCell>
                    <TableCell className="text-sm text-gray-600">
                      {new Date(app.dateApplied).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>

        {/* AI Match Distribution */}
        <Card>
          <CardHeader>
            <CardTitle>AI Match Distribution</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={200}>
              <PieChart>
                <Pie
                  data={scoreDistribution}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={80}
                  paddingAngle={2}
                  dataKey="value"
                >
                  {scoreDistribution.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Job Ads Performance */}
        <Card className="col-span-3">
          <CardHeader>
            <CardTitle>Job Ads Performance</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Job Title</TableHead>
                  <TableHead>Department</TableHead>
                  <TableHead>Location</TableHead>
                  <TableHead>Applications</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead></TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {mockJobAds.slice(0, 5).map((job) => (
                  <TableRow key={job.id}>
                    <TableCell className="font-medium">{job.title}</TableCell>
                    <TableCell className="text-sm text-gray-600">{job.department}</TableCell>
                    <TableCell className="text-sm text-gray-600">{job.location}</TableCell>
                    <TableCell>
                      <Badge variant="outline">{job.applicationsCount}</Badge>
                    </TableCell>
                    <TableCell>
                      <Badge variant={job.status === 'active' ? 'default' : 'secondary'}>
                        {job.status}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <Button variant="ghost" size="sm" asChild>
                        <Link to={`/admin/jobs/${job.id}`}>View</Link>
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
