import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Switch } from '../../components/ui/switch';
import { Separator } from '../../components/ui/separator';

export function Settings() {
  return (
    <div className="p-8 space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-semibold text-gray-900">Settings</h1>
        <p className="text-sm text-gray-500 mt-1">
          Manage your account and application preferences
        </p>
      </div>

      <div className="grid gap-6 max-w-4xl">
        {/* Account Settings */}
        <Card>
          <CardHeader>
            <CardTitle>Account Settings</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="name">Full Name</Label>
                <Input id="name" defaultValue="HR Admin" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input id="email" type="email" defaultValue="admin@company.com" />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="role">Role</Label>
              <Input id="role" defaultValue="HR Manager" disabled />
            </div>
            <div className="pt-4">
              <Button>Update Account</Button>
            </div>
          </CardContent>
        </Card>

        {/* Notification Settings */}
        <Card>
          <CardHeader>
            <CardTitle>Notification Preferences</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between">
              <div className="space-y-1">
                <p className="font-medium text-sm">New Applications</p>
                <p className="text-xs text-gray-500">
                  Get notified when new applications are submitted
                </p>
              </div>
              <Switch defaultChecked />
            </div>
            <Separator />
            <div className="flex items-center justify-between">
              <div className="space-y-1">
                <p className="font-medium text-sm">AI Processing Complete</p>
                <p className="text-xs text-gray-500">
                  Notifications when AI finishes evaluating candidates
                </p>
              </div>
              <Switch defaultChecked />
            </div>
            <Separator />
            <div className="flex items-center justify-between">
              <div className="space-y-1">
                <p className="font-medium text-sm">Weekly Summary</p>
                <p className="text-xs text-gray-500">
                  Receive weekly recruitment activity summary
                </p>
              </div>
              <Switch />
            </div>
          </CardContent>
        </Card>

        {/* AI Settings */}
        <Card>
          <CardHeader>
            <CardTitle>AI Configuration</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="threshold">Minimum Match Score Threshold</Label>
              <Input 
                id="threshold" 
                type="number" 
                min="0" 
                max="100" 
                defaultValue="70" 
              />
              <p className="text-xs text-gray-500">
                Applications below this score will be flagged for review
              </p>
            </div>
            <div className="flex items-center justify-between">
              <div className="space-y-1">
                <p className="font-medium text-sm">Auto-reject Low Scores</p>
                <p className="text-xs text-gray-500">
                  Automatically reject applications below threshold
                </p>
              </div>
              <Switch />
            </div>
            <div className="pt-4">
              <Button>Save AI Settings</Button>
            </div>
          </CardContent>
        </Card>

        {/* Security */}
        <Card>
          <CardHeader>
            <CardTitle>Security</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <Button variant="outline">Change Password</Button>
            <Separator />
            <div className="flex items-center justify-between">
              <div className="space-y-1">
                <p className="font-medium text-sm">Two-Factor Authentication</p>
                <p className="text-xs text-gray-500">
                  Add an extra layer of security to your account
                </p>
              </div>
              <Button variant="outline" size="sm">Enable</Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
