import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Badge } from '../../components/ui/badge';
import { Plus, Calendar, Edit, Briefcase, ChevronLeft, ChevronRight } from 'lucide-react';
import { mockCampaigns, mockJobAds } from '../../data/mockData';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '../../components/ui/dialog';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../components/ui/select';
import { Separator } from '../../components/ui/separator';

type TimeGranularity = 'day' | 'week' | 'month' | 'year';

export function Campaigns() {
  const [timeGranularity, setTimeGranularity] = useState<TimeGranularity>('month');
  const [currentDate, setCurrentDate] = useState(new Date('2026-03-11'));
  const [selectedCampaign, setSelectedCampaign] = useState<string | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);

  const getJobAdsForCampaign = (jobAdIds: string[]) => {
    return mockJobAds.filter(job => jobAdIds.includes(job.id));
  };

  // Generate timeline dates based on granularity
  const generateTimelineDates = () => {
    const dates: Date[] = [];
    let start: Date;
    let count: number;

    switch (timeGranularity) {
      case 'day':
        start = new Date(currentDate);
        start.setDate(start.getDate() - 7);
        count = 15;
        for (let i = 0; i < count; i++) {
          const date = new Date(start);
          date.setDate(start.getDate() + i);
          dates.push(date);
        }
        break;
      case 'week':
        start = new Date(currentDate);
        start.setDate(start.getDate() - start.getDay()); // Start of week
        start.setDate(start.getDate() - 21); // Go back 3 weeks
        count = 8;
        for (let i = 0; i < count; i++) {
          const date = new Date(start);
          date.setDate(start.getDate() + (i * 7));
          dates.push(date);
        }
        break;
      case 'month':
        start = new Date(currentDate);
        start.setMonth(start.getMonth() - 3);
        start.setDate(1);
        count = 7;
        for (let i = 0; i < count; i++) {
          const date = new Date(start);
          date.setMonth(start.getMonth() + i);
          dates.push(date);
        }
        break;
      case 'year':
        start = new Date(currentDate);
        start.setFullYear(start.getFullYear() - 1);
        start.setMonth(0);
        start.setDate(1);
        count = 3;
        for (let i = 0; i < count; i++) {
          const date = new Date(start);
          date.setFullYear(start.getFullYear() + i);
          dates.push(date);
        }
        break;
    }
    return dates;
  };

  const timelineDates = generateTimelineDates();

  // Calculate position and width for campaign bars
  const getCampaignPosition = (startDate: string, endDate: string) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const timelineStart = timelineDates[0];
    const timelineEnd = timelineDates[timelineDates.length - 1];

    const totalDays = (timelineEnd.getTime() - timelineStart.getTime()) / (1000 * 60 * 60 * 24);
    const startDays = (start.getTime() - timelineStart.getTime()) / (1000 * 60 * 60 * 24);
    const durationDays = (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);

    const left = Math.max(0, (startDays / totalDays) * 100);
    const width = Math.min((durationDays / totalDays) * 100, 100 - left);

    return { left: `${left}%`, width: `${width}%` };
  };

  const formatDateLabel = (date: Date) => {
    switch (timeGranularity) {
      case 'day':
        return date.toLocaleDateString('hu-HU', { month: 'short', day: 'numeric' });
      case 'week':
        const weekEnd = new Date(date);
        weekEnd.setDate(weekEnd.getDate() + 6);
        return `${date.toLocaleDateString('hu-HU', { month: 'short', day: 'numeric' })}`;
      case 'month':
        return date.toLocaleDateString('hu-HU', { month: 'short', year: 'numeric' });
      case 'year':
        return date.getFullYear().toString();
    }
  };

  const navigateTimeline = (direction: 'prev' | 'next') => {
    const newDate = new Date(currentDate);
    switch (timeGranularity) {
      case 'day':
        newDate.setDate(newDate.getDate() + (direction === 'next' ? 7 : -7));
        break;
      case 'week':
        newDate.setDate(newDate.getDate() + (direction === 'next' ? 28 : -28));
        break;
      case 'month':
        newDate.setMonth(newDate.getMonth() + (direction === 'next' ? 3 : -3));
        break;
      case 'year':
        newDate.setFullYear(newDate.getFullYear() + (direction === 'next' ? 1 : -1));
        break;
    }
    setCurrentDate(newDate);
  };

  const handleCampaignClick = (campaignId: string) => {
    setSelectedCampaign(campaignId);
    setEditDialogOpen(true);
  };

  const selectedCampaignData = mockCampaigns.find(c => c.id === selectedCampaign);

  return (
    <div className="p-8 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold text-gray-900">Kampányok</h1>
          <p className="text-sm text-gray-500 mt-1">
            Idővonalas nézet a toborzási kampányokhoz
          </p>
        </div>
        <Button>
          <Plus className="w-4 h-4 mr-2" />
          Új Kampány
        </Button>
      </div>

      {/* Timeline Controls */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Label className="text-sm font-medium">Nézet:</Label>
              <div className="flex gap-1">
                <Button
                  variant={timeGranularity === 'day' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setTimeGranularity('day')}
                >
                  Nap
                </Button>
                <Button
                  variant={timeGranularity === 'week' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setTimeGranularity('week')}
                >
                  Hét
                </Button>
                <Button
                  variant={timeGranularity === 'month' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setTimeGranularity('month')}
                >
                  Hónap
                </Button>
                <Button
                  variant={timeGranularity === 'year' ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setTimeGranularity('year')}
                >
                  Év
                </Button>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <Button variant="outline" size="icon" onClick={() => navigateTimeline('prev')}>
                <ChevronLeft className="w-4 h-4" />
              </Button>
              <span className="text-sm font-medium min-w-32 text-center">
                {currentDate.toLocaleDateString('hu-HU', { 
                  month: 'long', 
                  year: 'numeric' 
                })}
              </span>
              <Button variant="outline" size="icon" onClick={() => navigateTimeline('next')}>
                <ChevronRight className="w-4 h-4" />
              </Button>
              <Button 
                variant="ghost" 
                size="sm"
                onClick={() => setCurrentDate(new Date('2026-03-11'))}
              >
                Ma
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Timeline View */}
      <Card>
        <CardContent className="p-6">
          <div className="space-y-6">
            {/* Timeline Header - Dates */}
            <div className="flex items-center gap-2">
              <div className="w-48 flex-shrink-0" /> {/* Campaign name column */}
              <div className="flex-1 relative">
                <div className="flex border-b border-gray-200">
                  {timelineDates.map((date, index) => (
                    <div
                      key={index}
                      className="flex-1 text-center py-2 text-xs font-medium text-gray-600 border-l border-gray-200 first:border-l-0"
                    >
                      {formatDateLabel(date)}
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Campaign Rows */}
            <div className="space-y-4">
              {mockCampaigns.map((campaign) => {
                const position = getCampaignPosition(campaign.startDate, campaign.endDate);
                const jobAds = getJobAdsForCampaign(campaign.jobAds);
                const isVisible = position.width !== '0%';

                if (!isVisible) return null;

                return (
                  <div key={campaign.id} className="flex items-center gap-2">
                    {/* Campaign Info */}
                    <div className="w-48 flex-shrink-0">
                      <div className="space-y-1">
                        <div className="flex items-center gap-2">
                          <span className="font-medium text-sm truncate">
                            {campaign.name}
                          </span>
                        </div>
                        <div className="flex items-center gap-2">
                          <Badge 
                            variant={
                              campaign.status === 'active' ? 'default' :
                              campaign.status === 'completed' ? 'secondary' :
                              'outline'
                            }
                            className="text-xs"
                          >
                            {campaign.status === 'active' ? 'Aktív' : 
                             campaign.status === 'completed' ? 'Befejezett' : 
                             'Tervezett'}
                          </Badge>
                          <span className="text-xs text-gray-500">
                            {jobAds.length} állás
                          </span>
                        </div>
                      </div>
                    </div>

                    {/* Timeline Bar */}
                    <div className="flex-1 relative h-16 bg-gray-50 rounded-lg border border-gray-200">
                      {/* Grid lines */}
                      <div className="absolute inset-0 flex">
                        {timelineDates.map((_, index) => (
                          <div
                            key={index}
                            className="flex-1 border-l border-gray-200 first:border-l-0"
                          />
                        ))}
                      </div>

                      {/* Campaign Bar */}
                      <div
                        className="absolute top-2 bottom-2 rounded-md cursor-pointer transition-all hover:shadow-md group"
                        style={{
                          left: position.left,
                          width: position.width,
                          backgroundColor: 
                            campaign.status === 'active' ? '#3b82f6' :
                            campaign.status === 'completed' ? '#10b981' :
                            '#6b7280',
                        }}
                        onClick={() => handleCampaignClick(campaign.id)}
                      >
                        <div className="h-full px-3 flex items-center justify-between text-white">
                          <div className="flex items-center gap-2 min-w-0">
                            <Calendar className="w-3 h-3 flex-shrink-0" />
                            <span className="text-xs font-medium truncate">
                              {campaign.name}
                            </span>
                          </div>
                          <Edit className="w-3 h-3 opacity-0 group-hover:opacity-100 transition-opacity flex-shrink-0" />
                        </div>
                      </div>

                      {/* Today marker */}
                      {(() => {
                        const today = new Date();
                        const timelineStart = timelineDates[0];
                        const timelineEnd = timelineDates[timelineDates.length - 1];
                        
                        if (today >= timelineStart && today <= timelineEnd) {
                          const totalDays = (timelineEnd.getTime() - timelineStart.getTime()) / (1000 * 60 * 60 * 24);
                          const todayDays = (today.getTime() - timelineStart.getTime()) / (1000 * 60 * 60 * 24);
                          const todayPosition = (todayDays / totalDays) * 100;

                          return (
                            <div
                              className="absolute top-0 bottom-0 w-0.5 bg-red-500 z-10"
                              style={{ left: `${todayPosition}%` }}
                            >
                              <div className="absolute -top-1 left-1/2 -translate-x-1/2 w-2 h-2 bg-red-500 rounded-full" />
                            </div>
                          );
                        }
                        return null;
                      })()}
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Legend */}
            <div className="flex items-center justify-center gap-6 pt-4 border-t border-gray-200">
              <div className="flex items-center gap-2">
                <div className="w-4 h-4 bg-blue-600 rounded" />
                <span className="text-xs text-gray-600">Aktív</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-4 h-4 bg-green-600 rounded" />
                <span className="text-xs text-gray-600">Befejezett</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-4 h-4 bg-gray-600 rounded" />
                <span className="text-xs text-gray-600">Tervezett</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-0.5 h-4 bg-red-500" />
                <span className="text-xs text-gray-600">Mai nap</span>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Edit Campaign Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent className="max-w-2xl">
          {selectedCampaignData && (
            <>
              <DialogHeader>
                <DialogTitle>Kampány szerkesztése</DialogTitle>
              </DialogHeader>

              <div className="space-y-6">
                <div className="space-y-2">
                  <Label>Kampány neve</Label>
                  <Input defaultValue={selectedCampaignData.name} />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>Kezdés dátuma</Label>
                    <Input 
                      type="date" 
                      defaultValue={selectedCampaignData.startDate}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Befejezés dátuma</Label>
                    <Input 
                      type="date" 
                      defaultValue={selectedCampaignData.endDate}
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label>Státusz</Label>
                  <Select defaultValue={selectedCampaignData.status}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="active">Aktív</SelectItem>
                      <SelectItem value="completed">Befejezett</SelectItem>
                      <SelectItem value="scheduled">Tervezett</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <Separator />

                <div>
                  <Label className="text-sm font-semibold mb-3 block">
                    Hozzárendelt állások ({getJobAdsForCampaign(selectedCampaignData.jobAds).length})
                  </Label>
                  <div className="space-y-2 max-h-60 overflow-y-auto">
                    {getJobAdsForCampaign(selectedCampaignData.jobAds).map(job => (
                      <div 
                        key={job.id}
                        className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border border-gray-200"
                      >
                        <div className="flex items-center gap-3">
                          <Briefcase className="w-4 h-4 text-gray-600" />
                          <div>
                            <p className="font-medium text-sm">{job.title}</p>
                            <p className="text-xs text-gray-600">{job.department} • {job.location}</p>
                          </div>
                        </div>
                        <Badge variant="outline">{job.applicationsCount} pályázat</Badge>
                      </div>
                    ))}
                  </div>
                  <Button variant="outline" size="sm" className="w-full mt-3">
                    <Plus className="w-4 h-4 mr-2" />
                    Állás hozzáadása
                  </Button>
                </div>

                <div className="flex gap-3">
                  <Button className="flex-1">Mentés</Button>
                  <Button variant="outline" className="flex-1" onClick={() => setEditDialogOpen(false)}>
                    Mégse
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
