#!/usr/bin/python
import sys
from mininet.topo import Topo
from mininet.net import Mininet
from mininet.util import dumpNodeConnections
from mininet.log import setLogLevel
from mininet.link import TCLink
from mininet.cli import CLI

class SingleSwitchTopo(Topo):
    "Single switch connected to n hosts."
    def build(self, n=2):
        linkops = dict(bw=10, delay='5ms',  max_queue_size=1000, use_htb=True)

        if sys.argv[1] == 'star':
            # create center of star
            center = self.addSwitch('s1')
            host = self.addHost('h1')
            self.addLink(host,center, **linkops)
            
            # create the connections to the center
            for h in range(n-1):
                switch = self.addSwitch('s%s' % (h+2) )
                host = self.addHost('h%s' % (h + 2))
                self.addLink(host, switch, **linkops)
                self.addLink(center, switch, **linkops)

        else:
            # create first 
            previous = self.addSwitch('s1')
            host = self.addHost('h1')
            self.addLink(host,previous, **linkops)

            # create the rest of the connections
            for h in range(n-1):
                switch = self.addSwitch('s%s' % (h+2) )
                host = self.addHost('h%s' % (h + 2))
                self.addLink(host, switch, **linkops)
                self.addLink(previous, switch, **linkops)
                previous = switch
            
def simpleTest():
    "Create and test a simple network"
    number_of_hosts = 9
    topo = SingleSwitchTopo(n=number_of_hosts)
    net = Mininet(topo,link = TCLink)
    net.start()
    print "Dumping host connections"
    dumpNodeConnections(net.hosts)
    print "Testing network connectivity"
    net.pingAll()

    #####################
    # Begin custom cmds #
    ##                 ##
    ttr = '3000'
    ttl = '2'
    for x in range(1,number_of_hosts+1):
        net.get('h%s'%x).cmd('rmiregistry &')
    for x in range(1,number_of_hosts+1):
        print 'starting command: java main.java.peer.ClientDriver ./tests/test%s %s %s %s %s 10.0.0.%s < ../topologies/topo/input_%s.txt > ../topologies/topo/out_%s.txt 2>&1 &' % (x,topology,ttr,sys.argv[2],ttl,x,x,x)
        net.get('h%s'%x).cmd('java main.java.peer.ClientDriver ./tests/test%s %s %s %s %s 10.0.0.%s < ../topologies/topo/input_%s.txt > ../topologies/topo/out_%s.txt 2>&1 &' % (x,topology,ttr,sys.argv[2],ttl,x,x,x))
    
    ##                 ##
    #  End custom cmds  #
    #####################
    
    CLI(net)
    net.stop()
        
if __name__ == '__main__':
    # Tell mininet to print useful information
    if len(sys.argv) < 3:
        print "usage: \n\t arg1: star or linear \n\t arg2: push or pull"
        exit()
    global topology
    topology = sys.argv[1]
    setLogLevel('info')
    simpleTest()
