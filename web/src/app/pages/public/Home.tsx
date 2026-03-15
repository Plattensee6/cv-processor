import { Button } from '../../components/ui/button';
import { Card, CardContent } from '../../components/ui/card';
import { ArrowRight, Users, Award, Globe, Heart } from 'lucide-react';
import { Link } from 'react-router';

export function Home() {
  const values = [
    {
      icon: Users,
      title: 'Collaborative Culture',
      description: 'Work with talented people who value teamwork and innovation.',
    },
    {
      icon: Award,
      title: 'Growth Opportunities',
      description: 'Develop your skills and advance your career with us.',
    },
    {
      icon: Globe,
      title: 'Global Impact',
      description: 'Build products that make a difference worldwide.',
    },
    {
      icon: Heart,
      title: 'Work-Life Balance',
      description: 'We believe in flexible work and taking care of our people.',
    },
  ];

  return (
    <div>
      {/* Hero Section */}
      <section className="bg-gradient-to-b from-blue-50 to-white">
        <div className="max-w-7xl mx-auto px-6 py-24">
          <div className="max-w-3xl">
            <h1 className="text-5xl font-bold text-gray-900 mb-6">
              Build the Future with TechCorp
            </h1>
            <p className="text-xl text-gray-600 mb-8">
              Join our team of innovators, creators, and problem solvers. 
              We're building technology that transforms how people work and live.
            </p>
            <div className="flex gap-4">
              <Button size="lg" asChild>
                <Link to="/careers">
                  View Open Positions
                  <ArrowRight className="w-5 h-5 ml-2" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link to="/about">Learn More About Us</Link>
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Company Introduction */}
      <section className="py-24 bg-white">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-2 gap-16 items-center">
            <div>
              <h2 className="text-3xl font-bold text-gray-900 mb-6">
                Who We Are
              </h2>
              <p className="text-lg text-gray-600 mb-6">
                TechCorp is a leading technology company dedicated to creating innovative 
                solutions that empower businesses and individuals around the world.
              </p>
              <p className="text-lg text-gray-600 mb-6">
                Since our founding, we've grown to a team of over 1,000 talented professionals 
                across 15 countries, all working together to push the boundaries of what's possible.
              </p>
              <div className="grid grid-cols-3 gap-6 mt-8">
                <div>
                  <div className="text-3xl font-bold text-blue-600 mb-2">1000+</div>
                  <div className="text-sm text-gray-600">Team Members</div>
                </div>
                <div>
                  <div className="text-3xl font-bold text-blue-600 mb-2">15</div>
                  <div className="text-sm text-gray-600">Countries</div>
                </div>
                <div>
                  <div className="text-3xl font-bold text-blue-600 mb-2">50M+</div>
                  <div className="text-sm text-gray-600">Users Served</div>
                </div>
              </div>
            </div>
            <div className="bg-gradient-to-br from-blue-100 to-blue-50 rounded-2xl h-96 flex items-center justify-center">
              <div className="text-6xl">🏢</div>
            </div>
          </div>
        </div>
      </section>

      {/* Company Values */}
      <section className="py-24 bg-gray-50">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Our Values
            </h2>
            <p className="text-lg text-gray-600 max-w-2xl mx-auto">
              The principles that guide everything we do
            </p>
          </div>

          <div className="grid grid-cols-4 gap-6">
            {values.map((value, index) => {
              const Icon = value.icon;
              return (
                <Card key={index}>
                  <CardContent className="pt-6">
                    <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
                      <Icon className="w-6 h-6 text-blue-600" />
                    </div>
                    <h3 className="font-semibold text-gray-900 mb-2">
                      {value.title}
                    </h3>
                    <p className="text-sm text-gray-600">
                      {value.description}
                    </p>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-24 bg-blue-600">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h2 className="text-4xl font-bold text-white mb-6">
            Ready to Join Our Team?
          </h2>
          <p className="text-xl text-blue-100 mb-8 max-w-2xl mx-auto">
            Explore our open positions and find the perfect role for you.
          </p>
          <Button size="lg" variant="secondary" asChild>
            <Link to="/careers">
              View Career Opportunities
              <ArrowRight className="w-5 h-5 ml-2" />
            </Link>
          </Button>
        </div>
      </section>
    </div>
  );
}
