/**
 * Job App Controller for the LogIsland UI
 */

export default [ 'JobsDataService', 'ProcessorsDataService', 'ListService', 'AppSettings', '$mdSidenav', '$log', AppJobsController ];

function AppJobsController(JobsDataService, ProcessorsDataService, ListService, AppSettings, $mdSidenav, $log) {
    var self = this;

    self.version              = AppSettings.version;
    self.jobs                 = JobsDataService.query(function() { (self.jobs.length>0) ? self.selectedJob = self.jobs[0] : self.selectedJob = null   });
    self.selectedJob          = null;
    self.selectedProcessor    = null;
    self.addJob               = addJob;

    self.menuItems            = [   {name: "Start", direction: "right", icon: "play"},
                                    {name: "Stop", direction: "right", icon: "stop"}];
    self.isOpen = true;

    self.toggleList       = ListService.toggle;
    self.selectJob        = selectJob;

    self.querySearch      = querySearch;
    self.selectedItemChange = selectedItemChange;
    self.searchTextChange = searchTextChange;

    self.newJobTemplate = {name: "newJobTemplate", streams: [{"name": "[Stream name]", "component": "comp1", "config": [], "processors": []}]};

    function selectJob ( job ) {
        self.selectedJob = angular.isNumber(job) ? self.jobs[job] : job;
        if(self.selectedJob.streams.length > 0) {
            self.selectedStream = self.selectedJob.streams[0];
        }
    }

    function addJob() {
        $log.debug("add job not implemented");
    }

    function querySearch (query) {
        var results = query
                ? self.jobs.filter( createFilterFor(query) )
                : self.jobs;
        return results;
    }

    function createFilterFor(query) {
        var lowercaseQuery = angular.lowercase(query);

        return function filterFn(job) {
            return (job.name.indexOf(lowercaseQuery) === 0);
        };
    }

    function searchTextChange(text) {
          $log.info('Text changed to ' + text);
    }

    function selectedItemChange(job) {
        selectJob(job);
    }
}