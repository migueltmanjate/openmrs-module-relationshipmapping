function MappingHistoryCtrl($scope,$routeParams, $data, $route, $translate,$localeService) {

    $scope.pageSize = 10;
    $scope.paginationMaxSize = 10;
    $scope.currentPage = 1;
    $scope.totalItems = 0;
    $scope.loadPaginationStub = false;
    $scope.isServiceRunning = false;

    $localeService.getUserLocale().then(function (response) {
        var serverData = response.data.locale;
        $translate.use(serverData).then(function () {
            $scope.loadPaginationStub = true;
        });
    });

    $data.getExecutionCycles($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.executionCycles = serverData.objects;
        $scope.noOfPages = serverData.pages;
        $scope.totalItems = serverData.totalItems;
        $('#wait').hide();
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $data.getExecutionCycles($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.executionCycles = serverData.objects;
                $scope.noOfPages = serverData.pages;
                $scope.totalItems = serverData.totalItems;
                $('#wait').hide();
            });
        }
    }, true);

    $scope.runMappingTask = function () {
        $scope.isServiceRunning = true;
        $scope.wasServiceRunningAnotherTask = false;
        $data.runMappingTask().
        then(function (response) {
            $scope.isServiceRunning = false;
            $scope.wasServiceRunningAnotherTask = response.data.wasServiceRunningAnotherTask;
            if(!$scope.wasServiceRunningAnotherTask){
                $scope.refreshList();
            }

            $scope.didServiceRunSuccessfully = true;
        },function (response) {
            $scope.isServiceRunning = false;
            $scope.wasServiceRunningAnotherTask = false;
            $scope.didServiceRunSuccessfully = true;
            $scope.refreshList();
        });
    };
    
    $scope.refreshList = function () {
        $route.reload();
    }

}
